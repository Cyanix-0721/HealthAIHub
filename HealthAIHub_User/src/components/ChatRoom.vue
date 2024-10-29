<template>
  <div class="chat-room">
    <div class="message-area">
      <div v-for="message in messages" :key="message.id" class="message" :class="{ 'own-message': message.isOwn }">
        <el-avatar :size="32" :src="message.avatar" />
        <div class="message-content">
          <div class="message-sender">{{ message.sender }}</div>
          <div class="message-text">{{ message.text }}</div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
      </div>
    </div>
    <div class="input-area">
      <el-input v-model="newMessage" placeholder="输入消息..." @keyup.enter="sendMessage">
        <template #append>
          <el-button @click="sendMessage">发送</el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';

interface Message {
  id: number;
  sender: string;
  text: string;
  timestamp: Date;
  isOwn: boolean;
  avatar: string;
}

const props = defineProps<{
  isChatroom: boolean;
  friendId?: string;
}>();

const messages = ref<Message[]>([]);
const newMessage = ref<string>('');

const sendMessage = () => {
  if (newMessage.value.trim() === '') {
    ElMessage.warning('消息不能为空');
    return;
  }

  const message: Message = {
    id: Date.now(),
    sender: '我',
    text: newMessage.value,
    timestamp: new Date(),
    isOwn: true,
    avatar: 'https://example.com/my-avatar.jpg', // 替换为实际的用户头像
  };

  messages.value.push(message);
  newMessage.value = '';

  // 这里可以添加发送消息到服务器的逻辑
};

const formatTime = (date: Date) => {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
};

onMounted(() => {
  // 这里可以添加获取历史消息的逻辑
  // 例如：根据 props.isChatroom 和 props.friendId 从服务器获取相应的聊天记录
});

// 在脚本末尾添加这一行
defineExpose({ messages });
</script>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.message-area {
  flex-grow: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  display: flex;
  margin-bottom: 15px;
}

.message-content {
  margin-left: 10px;
  background-color: #f0f0f0;
  padding: 8px 12px;
  border-radius: 12px;
  max-width: 70%;
}

.own-message {
  flex-direction: row-reverse;
}

.own-message .message-content {
  margin-right: 10px;
  margin-left: 0;
  background-color: #e1f3fb;
}

.message-sender {
  font-weight: bold;
  margin-bottom: 4px;
}

.message-time {
  font-size: 0.8em;
  color: #888;
  text-align: right;
  margin-top: 4px;
}

.input-area {
  padding: 20px;
  background-color: #f9f9f9;
}
</style>
