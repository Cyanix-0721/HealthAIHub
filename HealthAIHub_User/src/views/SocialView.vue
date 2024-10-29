<template>
  <div class="social-view">
    <div class="social-sidebar" :style="{ width: sidebarWidth + 'px' }">
      <div class="search-bar">
        <el-input v-model="searchQuery" placeholder="搜索聊天室或好友" prefix-icon="el-icon-search" />
      </div>
      <div class="chat-list">
        <div class="chat-item" :class="{ active: activeChat === 'chatroom' }" @click="selectChat('chatroom')">
          <el-avatar :size="40" src="https://example.com/chatroom-avatar.jpg" />
          <div class="chat-info">
            <div class="chat-name">聊天室</div>
            <div class="last-message">{{ chatroomLastMessage }}</div>
          </div>
        </div>
        <div v-for="friend in filteredFriends" :key="friend.id" class="chat-item"
          :class="{ active: activeChat === String(friend.id) }" @click="selectChat(String(friend.id))">
          <el-avatar :size="40" :src="friend.avatar" />
          <div class="chat-info">
            <div class="chat-name">{{ friend.name }}</div>
            <div class="last-message">{{ friend.lastMessage }}</div>
          </div>
        </div>
      </div>
      <div class="resize-handle" @mousedown="startResize" @mousemove="resize" @mouseup="stopResize"
        @mouseleave="stopResize"></div>
    </div>
    <div class="chat-area">
      <chat-room v-if="activeChat !== ''" :is-chatroom="activeChat === 'chatroom'"
        :friend-id="activeChat !== 'chatroom' ? activeChat : undefined" />
      <div v-else class="no-chat-selected">
        请选择一个聊天开始交流
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import ChatRoom from '@/components/ChatRoom.vue'

const searchQuery = ref('')
const activeChat = ref('chatroom')  // 默认选中聊天室
const sidebarWidth = ref(300)
const isResizing = ref(false)
const startX = ref(0)
const chatroomLastMessage = ref('欢迎来到聊天室！')

const friends = ref([
  { id: 1, name: '张三', avatar: 'https://example.com/avatar1.jpg', lastMessage: '你好！' },
  { id: 2, name: '李四', avatar: 'https://example.com/avatar2.jpg', lastMessage: '晚上一起吃饭吗？' },
  // 添加更多好友数据...
])

const filteredFriends = computed(() => {
  return friends.value.filter(friend =>
    friend.name.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
})

const selectChat = (chatId: string) => {
  activeChat.value = chatId
}

const startResize = (e: MouseEvent) => {
  // 设置正在调整大小的标志为真
  isResizing.value = true
  // 记录鼠标开始拖动时的水平位置
  startX.value = e.clientX
}

const resize = (e: MouseEvent) => {
  if (!isResizing.value) return
  const diff = e.clientX - startX.value
  sidebarWidth.value = Math.max(200, Math.min(500, sidebarWidth.value + diff))
  startX.value = e.clientX
}

const stopResize = () => {
  isResizing.value = false
}

onMounted(() => {
  // 这里可以添加初始化逻辑，比如从API获取好友列表和最近消息
})
</script>

<style scoped>
.social-view {
  display: flex;
  height: 100%;
}

.social-sidebar {
  display: flex;
  flex-direction: column;
  border-right: 1px solid var(--el-border-color-light);
  position: relative;
}

.search-bar {
  padding: 10px;
}

.chat-list {
  flex-grow: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
}

.chat-item:hover {
  background-color: var(--el-fill-color-light);
}

.chat-item.active {
  background-color: var(--el-color-primary-light-9);
}

.chat-info {
  margin-left: 10px;
}

.chat-name {
  font-weight: bold;
}

.last-message {
  font-size: 0.9em;
  color: var(--el-text-color-secondary);
}

.resize-handle {
  position: absolute;
  right: -5px;
  top: 0;
  bottom: 0;
  width: 10px;
  cursor: col-resize;
}

.chat-area {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

.no-chat-selected {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  font-size: 1.2em;
  color: var(--el-text-color-secondary);
}
</style>
