'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  ShoppingBag, 
  Users, 
  DollarSign, 
  TrendingUp,
  Package,
  AlertCircle,
  ArrowUp,
  ArrowDown
} from 'lucide-react';
import { Card, CardHeader, CardContent } from '@/components/ui/Card';
import { dashboardApi } from '@/lib/api/dashboard';
import { DashboardOverviewDTO } from '@/lib/types/api';
import { formatPrice, formatNumber, formatPercentage } from '@/lib/utils/format';

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState<DashboardOverviewDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await dashboardApi.getDashboardOverview();
      if (response.success) {
        setDashboard(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch dashboard:', error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {[...Array(4)].map((_, i) => (
            <div key={i} className="card p-6 animate-pulse">
              <div className="h-4 bg-gray-200 rounded w-1/2 mb-4" />
              <div className="h-8 bg-gray-200 rounded w-3/4" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (!dashboard) {
    return (
      <div className="container-custom py-12 text-center">
        <p className="text-[#6f6e72]">Không thể tải dữ liệu dashboard</p>
      </div>
    );
  }

  const stats = [
    {
      title: 'Tổng Doanh Thu',
      value: formatPrice(dashboard.salesStats.totalRevenue),
      change: dashboard.salesStats.revenueGrowthPercentage,
      icon: DollarSign,
      color: 'text-green-600',
      bgColor: 'bg-green-100',
    },
    {
      title: 'Tổng Đơn Hàng',
      value: formatNumber(dashboard.salesStats.totalOrders),
      change: dashboard.salesStats.orderGrowthPercentage,
      icon: ShoppingBag,
      color: 'text-blue-600',
      bgColor: 'bg-blue-100',
    },
    {
      title: 'Tổng Người Dùng',
      value: formatNumber(dashboard.userStats.totalUsers),
      change: dashboard.userStats.userGrowthPercentage,
      icon: Users,
      color: 'text-purple-600',
      bgColor: 'bg-purple-100',
    },
    {
      title: 'Tổng Sản Phẩm',
      value: formatNumber(dashboard.productStats.totalProducts),
      change: 0,
      icon: Package,
      color: 'text-orange-600',
      bgColor: 'bg-orange-100',
    },
  ];

  return (
    <div className="container-custom py-12">
      <div className="mb-8">
        <h1 className="text-3xl md:text-4xl font-bold text-[#1d1d1f] mb-2">
          Tổng Quan Dashboard
        </h1>
        <p className="text-[#6f6e72]">
          Xem tổng quan về cửa hàng của bạn
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        {stats.map((stat, index) => (
          <motion.div
            key={stat.title}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
          >
            <Card>
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                    <stat.icon className={`w-6 h-6 ${stat.color}`} />
                  </div>
                  {stat.change !== 0 && (
                    <div className={`flex items-center text-sm ${
                      stat.change > 0 ? 'text-green-600' : 'text-red-600'
                    }`}>
                      {stat.change > 0 ? (
                        <ArrowUp className="w-4 h-4 mr-1" />
                      ) : (
                        <ArrowDown className="w-4 h-4 mr-1" />
                      )}
                      {formatPercentage(Math.abs(stat.change))}
                    </div>
                  )}
                </div>
                <h3 className="text-sm text-[#6f6e72] mb-1">{stat.title}</h3>
                <p className="text-2xl font-bold text-[#1d1d1f]">{stat.value}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      <div className="grid lg:grid-cols-2 gap-6 mb-8">
        {/* Recent Orders */}
        <Card>
          <CardHeader>
            <h2 className="text-xl font-bold text-[#1d1d1f]">Đơn Hàng Gần Đây</h2>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {dashboard.recentOrders.recentOrders.slice(0, 5).map((order) => (
                <div key={order.orderId} className="flex items-center justify-between py-3 border-b border-gray-100 last:border-0">
                  <div>
                    <p className="font-semibold text-[#1d1d1f]">
                      Đơn #{order.orderId}
                    </p>
                    <p className="text-sm text-[#6f6e72]">{order.customerName}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-[#1d1d1f]">
                      {formatPrice(order.totalAmount)}
                    </p>
                    <span className={`text-xs px-2 py-1 rounded-full ${
                      order.status === 'DELIVERED' 
                        ? 'bg-green-100 text-green-800'
                        : order.status === 'PENDING'
                        ? 'bg-yellow-100 text-yellow-800'
                        : 'bg-blue-100 text-blue-800'
                    }`}>
                      {order.status}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Top Products */}
        <Card>
          <CardHeader>
            <h2 className="text-xl font-bold text-[#1d1d1f]">Sản Phẩm Bán Chạy</h2>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {dashboard.topProducts.topProducts.slice(0, 5).map((product, index) => (
                <div key={product.productId} className="flex items-center gap-4 py-3 border-b border-gray-100 last:border-0">
                  <div className="w-8 h-8 rounded-full bg-[#1d1d1f] text-white flex items-center justify-center font-bold text-sm">
                    {index + 1}
                  </div>
                  <div className="flex-1">
                    <p className="font-semibold text-[#1d1d1f] line-clamp-1">
                      {product.productName}
                    </p>
                    <p className="text-sm text-[#6f6e72]">{product.categoryName}</p>
                  </div>
                  <div className="text-right">
                    <p className="font-semibold text-[#1d1d1f]">
                      {formatNumber(product.totalSold)} đã bán
                    </p>
                    <p className="text-sm text-[#6f6e72]">
                      {formatPrice(product.totalRevenue)}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Additional Stats */}
      <div className="grid md:grid-cols-3 gap-6">
        <Card>
          <CardHeader>
            <h3 className="font-bold text-[#1d1d1f]">Thống Kê Đơn Hàng</h3>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Chờ xử lý</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.orderStats.pendingOrders}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Đang xử lý</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.orderStats.processingOrders}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Đã giao</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.orderStats.deliveredOrders}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Đã hủy</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.orderStats.cancelledOrders}
                </span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <h3 className="font-bold text-[#1d1d1f]">Thống Kê Sản Phẩm</h3>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Đang hoạt động</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.productStats.activeProducts}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Tồn kho thấp</span>
                <span className="font-semibold text-orange-600">
                  {dashboard.productStats.lowStockProducts}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Hết hàng</span>
                <span className="font-semibold text-red-600">
                  {dashboard.productStats.outOfStockProducts}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Danh mục</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.productStats.totalCategories}
                </span>
              </div>
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <h3 className="font-bold text-[#1d1d1f]">Thống Kê Người Dùng</h3>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Đang hoạt động</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.userStats.activeUsers}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Mới tháng này</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.userStats.newUsersThisMonth}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Mới tuần này</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.userStats.newUsersThisWeek}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-[#6f6e72]">Quản trị viên</span>
                <span className="font-semibold text-[#1d1d1f]">
                  {dashboard.userStats.totalAdmins}
                </span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Alerts */}
      {dashboard.productStats.lowStockProducts > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="mt-6"
        >
          <div className="bg-orange-50 border border-orange-200 rounded-lg p-4 flex items-start gap-3">
            <AlertCircle className="w-5 h-5 text-orange-600 flex-shrink-0 mt-0.5" />
            <div>
              <h4 className="font-semibold text-orange-900 mb-1">Cảnh Báo Tồn Kho</h4>
              <p className="text-sm text-orange-800">
                Bạn có {dashboard.productStats.lowStockProducts} sản phẩm sắp hết hàng. 
                Cân nhắc nhập thêm hàng sớm.
              </p>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  );
}
