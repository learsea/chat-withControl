package com.sibu.chat.node.service;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sibu.chat.common.bean.po.ChatGroup;
import com.sibu.chat.common.bean.po.GroupUser;
import com.sibu.chat.common.bean.po.Notice;
import com.sibu.chat.common.bean.po.User;
import com.sibu.chat.common.bean.vo.tcp.TcpErrorResult;
import com.sibu.chat.common.bean.vo.tcp.TcpSuccessResult;
import com.sibu.chat.common.constant.Operation;
import com.sibu.chat.common.constant.Type;
import com.sibu.chat.common.exception.BusinessException;
import com.sibu.chat.common.utils.DateUtils;
import com.sibu.chat.common.utils.JSONUtils;
import com.sibu.chat.node.dao.ChatDao;
import com.sibu.chat.node.dao.GroupDao;
import com.sibu.chat.node.dao.NoticeDao;
import com.sibu.chat.node.redis.RUserDao;
import com.sibu.chat.node.service.interfaces.IGroupService;
import com.sibu.chat.node.service.interfaces.INoticeService;

/**
 * 群组服务层。
 * @author caishiyu
 *
 */
@Service
public class GroupService implements IGroupService {
	private Logger logger = Logger.getLogger(GroupService.class);
	@Autowired
	private GroupDao groupDao;
	@Autowired
	private ChatDao chatDao;
	@Autowired
	private NoticeDao noticeDao;
	@Autowired
	private RUserDao ruserDao;
	@Autowired
	private INoticeService noticeService;

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public String getGById(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		Integer groupId = in.getInteger("groupId");
		if (groupId == null) {
			throw new BusinessException("参数错误，groupId");
		}
		ChatGroup chatGroup = groupDao.getChatGroupByGroupId(groupId);
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("group", chatGroup);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String newGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		int loginId = UserService.checkUser(ctx).getId();
		JSONArray userIds = JSONUtils.getJSONArray(in, "userIds");
		userIds.remove(Integer.valueOf(loginId));
		if (userIds.size() < 1) {
			throw new BusinessException("参数错误，请传入userIds");
		}
		String groupName = in.getString("groupName");
		if (groupName == null) {
			throw new BusinessException("参数错误，请传入groupName");
		}
		// 新建群
		ChatGroup chatGroup = new ChatGroup();
		chatGroup.setGroupName(groupName);
		chatGroup.setIcon(in.getString("icon"));
		groupDao.newGroup(chatGroup);
		int groupId = chatGroup.getGroupId();
		// 添加群用户
		List<GroupUser> groupUsers = new ArrayList<GroupUser>();
		GroupUser groupAdmin = new GroupUser();
		groupAdmin.setGroupId(groupId);
		groupAdmin.setUserId(loginId);
		groupAdmin.setAdmin(1);
		groupUsers.add(groupAdmin);
		for (int i = 0; i < userIds.size(); i++) {
			int userId = userIds.getInteger(i);
			GroupUser groupUser = new GroupUser();
			groupUser.setGroupId(groupId);
			groupUser.setUserId(userId);
			groupUsers.add(groupUser);
		}
		try {
			groupDao.addGroupUsers(groupUsers);
			logger.info("用户" + loginId + "建立" + userIds.size() + "人群，群id" + groupId);
			// 发送通知
			noticeGroupUsers(loginId, userIds, genGroupNotice(loginId, null, groupId, Type.inviteToG));

			// 拼接返回结果
			JSONObject result = TcpSuccessResult.getResult(op);
			result.put("groupId", groupId);
			return JSONUtils.parse(result);
		} catch (DuplicateKeyException e) {
			throw new BusinessException("用户已经在群里");
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public String getGroups(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		List<Map<String, Object>> groups = groupDao.getGroups(loginUser.getId());
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("groups", groups);
		return JSONUtils.parse(result);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
	@Override
	public String getGUsers(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		int loginId = loginUser.getId();
		int groupId = GroupService.checkGroupId(JSONUtils.getInt(in, "groupId"));
		List<GroupUser> users = groupDao.getGroupUsesByGroupId(groupId);

		List<Integer> ids = new ArrayList<Integer>();
		List<GroupUser> admins = new ArrayList<GroupUser>();
		boolean existUser = false;
		for (GroupUser user : users) {
			if (user.getUserId() == loginId) {
				existUser = true;
			}
			if (user.getAdmin() > 0) {
				admins.add(user);
			}
			ids.add(user.getUserId());
		}
		if (!existUser) {
			throw new BusinessException("您不在该群，无法获取");
		}

		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("uids", ids);
		result.put("admins", admins);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String joinGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		int loginId = loginUser.getId();
		int groupId = checkGroupId(JSONUtils.getInt(in, "groupId"));
		try {
			groupDao.addGroupUser(new GroupUser(groupId, loginId));
			logger.info("用户" + loginId + "加入群：" + groupId);

			List<Integer> userIds = groupDao.getGroupUsesIdsByGroupId(groupId);
			// 发送通知
			noticeGroupUsers(loginId, userIds, genGroupNotice(loginId, null, groupId, Type.newUserG));

			// 拼接返回结果
			JSONObject result = TcpSuccessResult.getResult(op);
			result.put("groupId", groupId);
			return JSONUtils.parse(result);
		} catch (DuplicateKeyException e) {
			throw new BusinessException("您已经加入过该群");
		}
	}

	@Transactional
	@Override
	public String exitGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		Integer loginId = loginUser.getId();
		int groupId = checkGroupId(JSONUtils.getInt(in, "groupId"));
		GroupUser groupUser = new GroupUser();
		groupUser.setGroupId(groupId);
		groupUser.setUserId(loginId);
		if (groupDao.delGroupUser(groupUser) < 1) {
			throw new BusinessException("没有加入该群");
		}
		// 删除用户群消息
		chatDao.delAllGroupRecordsByUserId(loginId);
		// 用户已经全部退出，删除群
		if (groupDao.getGroupUsesByGroupId(groupId).size() == 0) {
			ChatGroup chatGroup = new ChatGroup();
			chatGroup.setGroupId(groupId);
			groupDao.dropGroup(chatGroup);
		}
		logger.info("用户" + loginId + "退出群：" + groupId);
		// 此时获取到的群用户不包括自己
		List<Integer> userIds = groupDao.getGroupUsesIdsByGroupId(groupId);
		// 发送通知
		noticeGroupUsers(loginId, userIds, genGroupNotice(loginId, null, groupId, Type.exitG));
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("groupId", groupId);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String dropGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		int groupId = checkGroupId(JSONUtils.getInt(in, "groupId"));
		ChatGroup chatGroup = new ChatGroup();
		chatGroup.setGroupId(groupId);
		// 删除群信息
		if (groupDao.dropGroup(chatGroup) < 1) {
			throw new BusinessException("没有该群");
		}
		List<Integer> userIds = groupDao.getGroupUsesIdsByGroupId(groupId);
		// 删除群用户
		groupDao.delGroupUsersByGroupId(groupId);
		// 删除所有群用户的群聊天记录
		chatDao.delAllGroupRecordsByGroupId(groupId);
		logger.info("用户" + loginUser.getId() + "解散群：" + groupId);

		noticeGroupUsers(loginUser.getId(), userIds, genGroupNotice(loginUser.getId(), null, groupId, Type.dropG));
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("groupId", groupId);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String alterGroup(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		// 校验是否登录
		User loginUser = UserService.checkUser(ctx);
		int groupId = checkGroupId(JSONUtils.getInt(in, "groupId"));
		String groupName = in.getString("groupName");
		if (groupName == null) {
			throw new BusinessException("参数错误，请传入groupName");
		}
		ChatGroup group = new ChatGroup();
		group.setGroupId(groupId);
		group.setGroupName(groupName);
		group.setIcon(in.getString("icon"));
		if (groupDao.alterGroup(group) < 1) {
			throw new BusinessException("没有该群");
		}

		logger.info("用户" + loginUser.getId() + "修改群：" + groupId);
		List<Integer> userIds = groupDao.getGroupUsesIdsByGroupId(groupId);
		noticeGroupUsers(loginUser.getId(), userIds, genGroupNotice(loginUser.getId(), null, groupId, Type.alterG));
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("groupId", groupId);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String kickout(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int kickedId = -1;
		try {
			kickedId = in.getIntValue("userId");
		} catch (Exception e) {
			throw new BusinessException("参数错误，检查是否传入userId");
		}
		int groupId = JSONUtils.getInt(in, "groupId");
		checkGroupId(groupId);
		List<Integer> userIds = groupDao.getGroupUsesIdsByGroupId(groupId);
		// 从群中删除该群用户
		if (groupDao.delGroupUser(new GroupUser(groupId, kickedId)) != 1) {
			// 群中没有该用户
			return JSONUtils.parse(TcpErrorResult.getResult(Operation.kickout, "没有该群或用户没有加入该群"));
		}
		// 删除用户群消息
		chatDao.delAllGroupRecordsByUserId(kickedId);
		// 发送通知
		noticeGroupUsers(loginUser.getId(), userIds, genGroupNotice(loginUser.getId(), kickedId, groupId, Type.beTG));
		// 拼接返回结果
		JSONObject result = TcpSuccessResult.getResult(op);
		result.put("groupId", groupId);
		result.put("userId", kickedId);
		return JSONUtils.parse(result);
	}

	@Transactional
	@Override
	public String inviteToG(Operation op, JSONObject in, ChannelHandlerContext ctx) {
		User loginUser = UserService.checkUser(ctx);
		int loginId = loginUser.getId();
		JSONArray userIds = JSONUtils.getJSONArray(in, "userIds");
		int groupId = JSONUtils.getInt(in, "groupId");
		if (userIds.size() == 0 || groupId == 0) {
			throw new BusinessException("参数错误，检查是否传入userId、groupId");
		}
		if (groupDao.existGroup(groupId) < 1) {
			throw new BusinessException("groupId不存在");
		}
		List<GroupUser> groupUsers = new ArrayList<GroupUser>();
		for (int i = 0; i < userIds.size(); i++) {
			groupUsers.add(new GroupUser(groupId, userIds.getInteger(i)));
		}

		try {
			groupDao.addGroupUsers(groupUsers);
			// 获取需要通知的id
			List<Integer> guIds = groupDao.getGroupUsesIdsByGroupId(groupId);
			// 拼接被操作人id
			String ruids = "";
			for (int i = 0; i < userIds.size(); i++) {
				if (i == userIds.size() - 1) {
					ruids += userIds.getString(i);
				} else {
					ruids += userIds.getString(i) + ",";
				}
			}
			// 发送通知
			noticeGroupUsers(loginId, guIds, genGroupNotice(loginId, ruids, groupId, Type.inviteToG));
			// 拼接返回结果
			JSONObject result = TcpSuccessResult.getResult(op);
			return JSONUtils.parse(result);
		} catch (DuplicateKeyException e) {
			throw new BusinessException("用户已经在群里");
		}
	}

	/**
	 * 校验groupid
	 * @param groupOpIV 传入参数
	 * @return 如果groupid为空则抛出异常，否则返回groupid
	 */
	public static int checkGroupId(int groupId) {
		if (groupId == 0) {
			throw new BusinessException("参数错误，请传入正确的groupId");
		}
		return groupId;
	}

	/**
	 * 给群所有用户发送通知
	 * @param loginId 登录用户id
	 * @param userIds 群用户id列表
	 * @param ov 群通知视图
	 * @param op 操作类型
	 * @return 返回字符串
	 */
	@Override
	@Transactional
	public void noticeGroupUsers(int loginId, List<?> userIdList, Notice notice) {
		for (Object ido : userIdList) {
			Integer id = (Integer) ido;
			if (id.equals(loginId)) {
				// 不用给T出用户的那个人发通知
				continue;
			}
			notice.setUserId(id);
			noticeService.notice(id, notice, true);
		}
	}

	/**
	 * 拼接群操作通知，有被操作人（如群主踢人）
	 * @param operatorUid 操作人id
	 * @param relatedUids 被操作人id串，多个id用,分隔
	 * @param relatedGid 相关群id
	 * @param type 操作类型
	 * @return 通知po
	 */
	private Notice genGroupNotice(int operatorUid, String relatedUids, int relatedGid, Type type) {
		Notice notice = new Notice();
		notice.setCreatetime(DateUtils.getTimeStamp());
		notice.setType(type.toString());
		notice.setRelatedGid(relatedGid);
		notice.setOperatorUid(operatorUid);
		notice.setRelatedUids(relatedUids);
		return notice;
	}

	/**
	 * 拼接群操作通知，有被操作人（如群主踢人）
	 * @param operatorUid 操作人id
	 * @param relatedUid 被操作人id串
	 * @param relatedGid 相关群id
	 * @param type 操作类型
	 * @return 通知po
	 */
	private Notice genGroupNotice(int operatorUid, int relatedUid, int relatedGid, Type type) {
		return genGroupNotice(operatorUid, relatedUid + "", relatedGid, type);
	}
}
