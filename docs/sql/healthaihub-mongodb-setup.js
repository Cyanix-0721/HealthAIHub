// 确保在MongoDB shell中选择了HealthAIHub数据库
db = db.getSiblingDB("health_ai_hub");

// 删除现有的集合（如果存在）
db.private_message.drop();
db.chatroom_message.drop();
db.notification.drop();
db.user_ai_interaction.drop();

// 创建集合并插入测试数据

// 私信集合
db.createCollection("private_message");
db.private_message.insertMany([
  {
    sender_id: "1",
    receiver_id: "2",
    content: "你好，最近身体如何？",
    is_deleted: false,
    timestamp: new Date(),
  },
  {
    sender_id: "2",
    receiver_id: "1",
    content: "挺好的，谢谢关心！",
    is_deleted: false,
    timestamp: new Date(),
  },
  {
    sender_id: "1",
    receiver_id: "3",
    content: "周末要一起运动吗？",
    is_deleted: false,
    timestamp: new Date(),
  },
]);

// 聊天室消息集合
db.createCollection("chatroom_message");
db.chatroom_message.insertMany([
  {
    user_id: "1",
    content: "大家好，欢迎来到健康交流室！",
    is_deleted: false,
    timestamp: new Date(),
  },
  {
    user_id: "2",
    content: "你好！我是新来的。",
    is_deleted: false,
    timestamp: new Date(),
  },
  {
    user_id: "3",
    content: "欢迎新成员！",
    is_deleted: false,
    timestamp: new Date(),
  },
]);

// 通知消息集合
db.createCollection("notification");
db.notification.insertMany([
  {
    user_id: "1",
    content: "您有一条新的好友请求",
    is_read: false,
    timestamp: new Date(),
  },
  {
    user_id: "2",
    content: "您的健康数据有新的更新",
    is_read: true,
    timestamp: new Date(),
  },
  {
    user_id: "3",
    content: "明天是您的体检预约日",
    is_read: false,
    timestamp: new Date(),
  },
]);

// 用户与大模型的交互记录集合
db.createCollection("user_ai_interaction");
db.user_ai_interaction.insertMany([
  {
    user_id: "1",
    session_id: "session1",
    context: [
      { role: "user", content: "我最近感觉很疲劳，有什么建议吗？" },
      {
        role: "assistant",
        content:
          "建议您注意休息，保证充足的睡眠时间。同时，可以适当增加一些轻度运动，如散步或瑜伽，有助于缓解疲劳。如果症状持续，建议您咨询医生。",
      },
    ],
    timestamp: new Date(),
  },
  {
    user_id: "2",
    session_id: "session2",
    context: [
      { role: "user", content: "如何保持健康的饮食习惯？" },
      {
        role: "assistant",
        content:
          "健康的饮食习惯包括：1. 均衡饮食，摄入各类营养素；2. 控制总热量摄入；3. 增加蔬菜水果摄入；4. 减少高脂肪、高糖食品；5. 规律进餐，不要skip meals。建议您根据自身情况逐步调整饮食结构。",
      },
    ],
    timestamp: new Date(),
  },
]);

// 创建索引
db.private_message.createIndex({
  sender_id: 1,
  receiver_id: 1,
  timestamp: -1,
});
db.chatroom_message.createIndex({ timestamp: -1 });
db.notification.createIndex({ user_id: 1, is_read: 1, timestamp: -1 });
db.user_ai_interaction.createIndex({
  user_id: 1,
  session_id: 1,
  timestamp: -1,
});

print("MongoDB数据库 HealthAIHub 初始化完成！");
