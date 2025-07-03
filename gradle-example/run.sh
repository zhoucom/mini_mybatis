#!/bin/bash

# Mini MyBatis Gradle示例项目 - 一键运行脚本

echo "============================================"
echo "🚀 Mini MyBatis Gradle示例项目启动脚本"
echo "============================================"

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境，请安装JDK 8+"
    exit 1
fi

# 检查Gradle环境
if ! command -v ./gradlew &> /dev/null; then
    echo "❌ 错误: 未找到Gradle Wrapper"
    exit 1
fi

echo "✅ Java环境检查通过"
echo "✅ Gradle环境检查通过"

# 检查Mini MyBatis Starter是否已安装
STARTER_PATH="$HOME/.m2/repository/com/example/mini-mybatis-spring-boot-starter/1.0.0"
if [ ! -d "$STARTER_PATH" ]; then
    echo "⚠️  警告: Mini MyBatis Starter未安装到本地Maven仓库"
    echo "📦 正在构建并安装Mini MyBatis Starter..."
    
    # 切换到starter目录并构建
    if [ -d "../mybatis" ]; then
        cd ../mybatis
        mvn clean install
        if [ $? -eq 0 ]; then
            echo "✅ Mini MyBatis Starter安装成功"
            cd ../gradle-example
        else
            echo "❌ Mini MyBatis Starter安装失败"
            exit 1
        fi
    else
        echo "❌ 错误: 找不到Mini MyBatis Starter源码目录"
        echo "请确保在正确的项目目录中运行此脚本"
        exit 1
    fi
else
    echo "✅ Mini MyBatis Starter已安装"
fi

echo ""
echo "🔨 正在构建Gradle项目..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo "✅ 项目构建成功"
else
    echo "❌ 项目构建失败"
    exit 1
fi

echo ""
echo "🚀 启动应用程序..."
echo "📱 应用将在 http://localhost:8080 启动"
echo "🗄️  H2数据库控制台: http://localhost:8080/h2-console"
echo "🔍 健康检查: http://localhost:8080/api/products/health"
echo ""
echo "按 Ctrl+C 停止应用"
echo ""

# 启动应用
./gradlew bootRun 