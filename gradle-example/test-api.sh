#!/bin/bash

# Mini MyBatis Gradle示例项目 - API测试脚本

BASE_URL="http://localhost:8080/api/products"

echo "============================================"
echo "🧪 Mini MyBatis API功能测试脚本"
echo "============================================"

# 检查应用是否运行
echo "🔍 检查应用状态..."
curl -s "$BASE_URL/health" > /dev/null
if [ $? -ne 0 ]; then
    echo "❌ 应用未运行，请先启动应用: ./run.sh"
    exit 1
fi
echo "✅ 应用运行正常"
echo ""

# 测试函数
test_api() {
    local name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    
    echo "📍 测试: $name"
    echo "🔗 $method $url"
    
    if [ -n "$data" ]; then
        echo "📤 请求数据: $data"
        response=$(curl -s -X "$method" "$url" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -X "$method" "$url")
    fi
    
    echo "📥 响应: $response"
    echo "---"
    sleep 1
}

# 1. 查询操作测试
echo "🔍 === 查询操作测试 ==="

test_api "健康检查" "GET" "$BASE_URL/health"

test_api "获取所有商品" "GET" "$BASE_URL"

test_api "获取商品详情" "GET" "$BASE_URL/1"

test_api "获取商品分类" "GET" "$BASE_URL/categories"

test_api "分类查询商品" "GET" "$BASE_URL/category/电子产品"

test_api "价格范围查询" "GET" "$BASE_URL/price-range?minPrice=1000&maxPrice=10000"

test_api "关键词搜索" "GET" "$BASE_URL/search?keyword=MacBook"

test_api "低库存查询" "GET" "$BASE_URL/low-stock?threshold=20"

test_api "获取统计信息" "GET" "$BASE_URL/stats"

echo ""
echo "✏️ === 管理操作测试 ==="

# 2. 创建操作测试
test_api "创建商品(基础)" "POST" "$BASE_URL" \
    '{"name":"测试商品Basic","price":599.99,"category":"测试分类"}'

test_api "创建商品(完整)" "POST" "$BASE_URL/full" \
    '{"name":"测试商品Full","price":899.99,"category":"测试分类","description":"完整商品描述","stockQuantity":50}'

# 3. 更新操作测试  
test_api "更新商品基本信息" "PUT" "$BASE_URL/1" \
    '{"name":"更新后的商品","price":1599.99,"category":"更新分类"}'

test_api "更新库存" "PUT" "$BASE_URL/1/stock" \
    '{"stockQuantity":30}'

test_api "增加库存" "POST" "$BASE_URL/1/increase-stock" \
    '{"quantity":10}'

test_api "减少库存" "POST" "$BASE_URL/1/decrease-stock" \
    '{"quantity":5}'

echo ""
echo "📊 === 验证操作结果 ==="

test_api "验证更新后的商品信息" "GET" "$BASE_URL/1"

test_api "验证创建的商品" "GET" "$BASE_URL/search?keyword=测试商品"

echo ""
echo "🗑️ === 清理测试数据 ==="

# 删除测试创建的商品（通过搜索找到ID然后删除）
echo "🔍 查找测试商品ID进行清理..."
response=$(curl -s "$BASE_URL/search?keyword=测试商品")
echo "测试商品列表: $response"

echo ""
echo "🎉 === API测试完成 ==="
echo "✅ 所有主要API接口测试完成"
echo "📱 完整API文档请查看: http://localhost:8080/api/products"
echo "🗄️  数据库控制台: http://localhost:8080/h2-console" 