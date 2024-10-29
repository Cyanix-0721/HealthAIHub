import { defineStore } from 'pinia'
import { ref, watch } from 'vue'
import { useWebSocket } from '@/service/websocket'

export const useAiChatStore = defineStore('aiChat', () => {
  const { socket, isConnected, connect, disconnect, sendMessage } =
    useWebSocket('ai-chat')
  const messages = ref([
    {
      role: 'assistant',
      content: '您好！我是您的健康助手。有什么可以帮助您的吗？',
    },
  ])
  const isLoading = ref(false)

  const initWebSocket = () => {
    connect()
    if (socket.value) {
      socket.value.onmessage = event => {
        console.log('收到消息:', event.data)
        messages.value.push({ role: 'assistant', content: event.data })
        isLoading.value = false
      }
    }
  }

  const sendChatMessage = (message: string) => {
    if (message.trim() && !isLoading.value) {
      messages.value.push({ role: 'user', content: message })
      isLoading.value = true
      console.log('发送消息:', message)
      sendMessage(message)
    }
  }

  watch(isConnected, newValue => {
    console.log('WebSocket连接状态变化:', newValue)
    if (newValue) {
      console.log('WebSocket已连接')
    } else {
      console.log('WebSocket已断开')
    }
  })

  watch(
    () => socket.value,
    newSocket => {
      console.log('WebSocket实例变化:', newSocket)
      if (newSocket) {
        console.log('新的WebSocket实例已创建')
      } else {
        console.log('WebSocket实例已销毁')
      }
    },
  )

  return {
    messages,
    isLoading,
    isConnected,
    initWebSocket,
    sendChatMessage,
    disconnect,
  }
})
