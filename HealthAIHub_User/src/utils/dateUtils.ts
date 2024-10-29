export function formatDateTime(dateTimeString: string): string {
  // 假设后端发送的是 ISO 8601 格式的字符串
  const date = new Date(dateTimeString)

  // 获取本地时间的年、月、日、时、分、秒
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  const hours = date.getHours().toString().padStart(2, '0')
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const seconds = date.getSeconds().toString().padStart(2, '0')

  // 返回格式化后的日期和时间字符串
  return `${year}/${month}/${day} ${hours}:${minutes}:${seconds}`
}
