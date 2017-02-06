package com.sibu.chat.node.service;

import io.netty.channel.ChannelHandlerContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Type;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.DateUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.dao.FriendDao;
import com.sibu.chat.node.dao.NoticeDao;
import com.sibu.chat.node.redis.RUserDao;
import com.sibu.chat.node.service.interfaces.IFriendService;
import com.sibu.chat.node.service.interfaces.INoticeService;

@Service
public class FriendService implements IFriendService {
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private RUserDao ruserDao;
	@Autowired
	private INoticeService noticeService;
	@Autowired
	private FriendDao friendDao;

	@Transactional
	@Override
	public String aggreF(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int friendId = -1;
		try {
			friendId = in.getIntValue("userId");
		} catch (Exception e) {
			throw new BusinessException("参数错误,userId");
		}
		int agree = JSONUtils.getInt(in, "agree");

		try {
			friendDao.addFriend(loginUser.getId(), friendId);
			friendDao.addFriend(friendId, loginUser.getId());
		} catch (DuplicateKeyException e) {
		}

		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(agree == 1 ? Type.aggreF.toString() : Type.disAggreF.toString());
		notice.setOperatorUid(loginUser.getId());
		notice.setUserId(friendId);
		// 发送通知
		noticeService.notice(friendId, notice, true);
		return JSONUtils.parse(TcpSuccessResult.getResult(op));
	}

	@Override
	public String addFriend(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int friendId = -1;
		try {
			friendId = in.getIntValue("friendId");
		} catch (Exception e) {
			throw new BusinessException("参数错误，检查是否传入userId");
		}

		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(Type.addFriend.toString());
		notice.setOperatorUid(loginUser.getId());
		notice.setContent(in.getString("content"));
		notice.setUserId(friendId);
		noticeService.notice(friendId, notice, true);
		return JSONUtils.parse(TcpSuccessResult.getResult(op));
	}

	@Transactional
	@Override
	public String delF(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int friendId = -1;
		try {
			friendId = in.getIntValue("friendId");
		} catch (Exception e) {
			throw new BusinessException("参数错误，检查是否传入userId");
		}

		friendDao.delFriend(loginUser.getId(), friendId);

		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(Type.delF.toString());
		notice.setOperatorUid(loginUser.getId());
		notice.setContent(in.getString("content"));
		notice.setUserId(friendId);
		noticeService.notice(friendId, notice, true);
		return JSONUtils.parse(TcpSuccessResult.getResult(op));
	}

	@Transactional
	@Override
	public String black(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int bid = -1;
		try {
			bid = in.getIntValue("bid");
		} catch (Exception e) {
			throw new BusinessException("请传bid");
		}
		int friendId = -1;
		try {
			friendId = in.getIntValue("friendId");
		} catch (Exception e) {
			throw new BusinessException("参数错误，检查是否传入userId");
		}

		friendDao.delFriend(loginUser.getId(), friendId);
		// 添加黑名单
		String bids = friendDao.getBids(loginUser.getId());
		boolean exist = false;
		if (bids != null) {
			if (bids.startsWith(bid + ",")) {
				exist = true;
			} else if (bids.endsWith("," + bid)) {
				exist = true;
			} else if (bids.contains("," + bid + ",")) {
				exist = true;
			} else {
				bids += "," + bid;
			}
		} else {
			bids = "" + bid;
		}
		if (!exist) {
			friendDao.updateBlacklist(loginUser.getId(), bids);
		}

		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(Type.black.toString());
		notice.setOperatorUid(loginUser.getId());
		notice.setContent(in.getString("content"));
		notice.setUserId(friendId);
		noticeService.notice(friendId, notice, true);
		return JSONUtils.parse(TcpSuccessResult.getResult(op));
	}
}
