<template>
  <div class="about-page">
    <el-card>
      <template #header>
        <span>关于项目</span>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="项目名称">智慧农业管理系统</el-descriptions-item>
        <el-descriptions-item label="版本">v0.0.1</el-descriptions-item>
        <el-descriptions-item label="前端技术栈">Vue 3 + TypeScript + Vite + Element Plus</el-descriptions-item>
        <el-descriptions-item label="后端技术栈">Spring Boot 3 + MyBatis-Plus + JDK 21</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">
          基于物联网技术的智慧农业综合管理平台，实现农业生产的智能化监控与管理。
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.about-page {
  max-width: 800px;
}
</style>
