import { ref } from 'vue'
import type { Ref } from 'vue'

const apiBasePath = import.meta.env.VITE_API_BASE_PATH || '/api'
const isDevelopment = import.meta.env.MODE === 'development'
const wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws'

let wsBaseUrl: string

if (isDevelopment) {
  const backendUrl =
    import.meta.env.VITE_DEV_SERVER_URL || window.location.origin
  wsBaseUrl = `${wsProtocol}://${new URL(backendUrl).host}/ws`
} else {
  // 在生产环境中，使用当前页面的主机名
  wsBaseUrl = `${wsProtocol}://${window.location.host}${apiBasePath}/ws`
}

export const useWebSocket = (endpoint: string) => {
  const socket: Ref<WebSocket | null> = ref(null)
  const isConnected = ref(false)

  const connect = () => {
    const token = localStorage.getItem('token')
    socket.value = new WebSocket(`${wsBaseUrl}/${endpoint}?token=${token}`)

    socket.value.onopen = () => {
      isConnected.value = true
      console.log('WebSocket connected')
    }

    socket.value.onclose = () => {
      isConnected.value = false
      console.log('WebSocket disconnected')
    }

    socket.value.onerror = error => {
      console.error('WebSocket error:', error)
    }
  }

  const disconnect = () => {
    if (socket.value) {
      socket.value.close()
    }
  }

  const sendMessage = (message: string) => {
    if (socket.value && socket.value.readyState === WebSocket.OPEN) {
      socket.value.send(message)
    } else {
      console.error('WebSocket is not connected')
    }
  }

  return {
    socket,
    isConnected,
    connect,
    disconnect,
    sendMessage,
  }
}
