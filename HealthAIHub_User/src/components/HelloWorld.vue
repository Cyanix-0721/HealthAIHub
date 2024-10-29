<script setup lang="ts">
defineProps({
  msg1: {
    type: String,
    required: true
  },
  msg2: {
    type: String,
    required: true
  }
})
</script>

<template>
  <div>
    <h1 class="title">{{ msg1 }}</h1>
    <h1 class="title">{{ msg2 }}</h1>
    <h2 class="subtitle">基于医疗大模型的个人健康助手</h2>
  </div>
</template>

<style scoped>
.title {
  font-size: 1.5em;
  text-align: center;
  color: var(--color-primary);
}

.subtitle {
  font-size: 1em;
  text-align: center;
  white-space: nowrap;
}
</style>
