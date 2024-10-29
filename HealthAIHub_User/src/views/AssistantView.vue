<template>
  <div class="chat-container">
    <div class="chat-messages" ref="chatMessagesRef">
      <div v-for="(message, index) in messages" :key="index" :class="['message', message.role]">
        <div class="avatar">
          <el-avatar :icon="message.role === 'user' ? UserFilled : Assistant" />
        </div>
        <div class="content">{{ message.content }}</div>
      </div>
    </div>
    <div class="chat-input">
      <el-input v-model="userInput" type="textarea" :rows="5" placeholder="输入您的问题..." @keyup.enter.ctrl="sendMessage" />
      <el-button type="primary" class="send-button" @click="sendMessage" :loading="isLoading">
        <el-icon>
          <ArrowRight />
        </el-icon>
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted, computed } from 'vue'
import { ElInput, ElButton, ElAvatar } from 'element-plus'
import { UserFilled, Service as Assistant, ArrowRight } from '@element-plus/icons-vue'
import { useAiChatStore } from '@/stores/aiChat'

const aiChatStore = useAiChatStore()
const userInput = ref('')
const chatMessagesRef = ref<HTMLElement | null>(null)
const messages = computed(() => aiChatStore.messages)
const isLoading = ref(false)

const sendMessage = async () => {
  if (userInput.value.trim() === '') return
  isLoading.value = true
  try {
    await aiChatStore.sendChatMessage(userInput.value.trim())
  } finally {
    isLoading.value = false
  }
  userInput.value = ''
  nextTick(() => {
    scrollToBottom()
  })
}

const scrollToBottom = () => {
  if (chatMessagesRef.value) {
    chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
  }
}

onMounted(() => {
  console.log('Initializing WebSocket')
  aiChatStore.initWebSocket()
  scrollToBottom()
})

onUnmounted(() => {
  aiChatStore.disconnect()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-messages {
  flex-grow: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  display: flex;
  margin-bottom: 20px;
}

.message .avatar {
  margin-right: 10px;
}

.message .content {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 10px;
  max-width: 70%;
}

.message.assistant .content {
  border-color: #b3e0ff;
}

.chat-input {
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.chat-input .el-input {
  width: 60%;
  margin-right: 10px;
}

.send-button {
  padding: 12px;
  height: auto;
  background-color: transparent;
  border: none;
}

:deep(.el-textarea__inner) {
  min-height: 100px !important;
}

:deep(.el-button.send-button .el-icon) {
  font-size: 24px;
  color: #409EFF;
}

:deep(.el-button.send-button:hover) {
  background-color: transparent;
}

:deep(.el-button.send-button:hover .el-icon) {
  color: #79bbff;
}
</style>
