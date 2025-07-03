#!/bin/bash

# Mini MyBatis 高级功能测试脚本
# 测试动态SQL、缓存、事务、安全性等企业级功能

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Mini MyBatis 高级功能测试"
echo "=========================================="

# 检查服务是否启动
echo "1. 检查服务状态..."
if ! curl -s "${BASE_URL}/api/products" > /dev/null; then
    echo "❌ 服务未启动，请先运行 ./run.sh"
    exit 1
fi
echo "✅ 服务正常运行"

echo ""
echo "2. 获取功能列表..."
curl -s "${BASE_URL}/api/advanced/features" | jq '.'

echo ""
echo "=========================================="
echo "3. 动态SQL功能测试"
echo "=========================================="

echo ""
echo "3.1 有条件查询（价格 >= 1000）"
curl -s "${BASE_URL}/api/advanced/smart-price?minPrice=1000" | jq '.'

echo ""
echo "3.2 无条件查询（动态SQL - 全部商品）"
curl -s "${BASE_URL}/api/advanced/smart-price" | jq '.'

echo ""
echo "=========================================="
echo "4. 缓存功能测试"
echo "=========================================="

echo ""
echo "4.1 缓存演示（多次查询同一商品）"
curl -s "${BASE_URL}/api/advanced/cache-demo/1" | jq '.'

echo ""
echo "=========================================="
echo "5. 事务功能测试"
echo "=========================================="

echo ""
echo "5.1 正常价格调整（事务提交）"
curl -X POST "${BASE_URL}/api/advanced/adjust-price" \
     -H "Content-Type: application/json" \
     -d '{"category": "Electronics", "multiplier": 1.1}' \
     -s | jq '.'

echo ""
echo "5.2 异常价格调整（事务回滚演示）"
curl -s "${BASE_URL}/api/advanced/exception-demo?type=transaction_rollback" | jq '.'

echo ""
echo "=========================================="
echo "6. 安全性功能测试"
echo "=========================================="

echo ""
echo "6.1 正常搜索"
curl -s "${BASE_URL}/api/advanced/safe-search?keyword=phone" | jq '.'

echo ""
echo "6.2 SQL注入尝试（安全防护演示）"
curl -s "${BASE_URL}/api/advanced/exception-demo?type=sql_injection" | jq '.'

echo ""
echo "6.3 恶意参数测试"
curl -s "${BASE_URL}/api/advanced/safe-search?keyword='; DROP TABLE products; --" | jq '.'

echo ""
echo "=========================================="
echo "7. 性能对比测试"
echo "=========================================="

echo ""
echo "7.1 缓存性能对比"
echo "第一次查询（数据库）:"
time curl -s "${BASE_URL}/api/products/1" > /dev/null

echo ""
echo "第二次查询（缓存）:"
time curl -s "${BASE_URL}/api/products/1" > /dev/null

echo ""
echo "=========================================="
echo "8. 错误处理测试"
echo "=========================================="

echo ""
echo "8.1 无效商品ID"
curl -s "${BASE_URL}/api/products/999" | jq '.'

echo ""
echo "8.2 无效参数"
curl -s "${BASE_URL}/api/advanced/smart-price?minPrice=invalid" | jq '.'

echo ""
echo "=========================================="
echo "测试完成！"
echo "=========================================="

echo ""
echo "📊 测试总结："
echo "✅ 动态SQL：根据参数动态生成查询"
echo "✅ 一级缓存：SqlSession级别缓存"
echo "✅ 二级缓存：全局共享缓存"
echo "✅ 事务管理：支持事务提交和回滚"
echo "✅ SQL注入防护：自动检测和阻止攻击"
echo "✅ 异常处理：友好的错误信息"
echo "✅ 参数安全：参数化查询防护"

echo ""
echo "🔗 更多测试endpoint："
echo "   GET  /api/advanced/features           - 功能列表"
echo "   GET  /api/advanced/smart-price        - 动态SQL测试"
echo "   GET  /api/advanced/cache-demo/{id}    - 缓存测试"
echo "   POST /api/advanced/adjust-price       - 事务测试"
echo "   GET  /api/advanced/safe-search        - 安全测试"
echo "   GET  /api/advanced/exception-demo     - 异常演示" 