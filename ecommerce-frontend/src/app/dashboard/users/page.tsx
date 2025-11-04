'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Users, 
  UserPlus, 
  Shield, 
  Search,
  Eye,
  Ban,
  CheckCircle,
  Mail,
  Calendar
} from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { Card, CardContent } from '@/components/ui/Card';
import { Pagination } from '@/components/common/Pagination';
import { usersApi } from '@/lib/api/users';
import { UserResponse } from '@/lib/types/api';
import { formatDate } from '@/lib/utils/format';
import { toast } from 'react-hot-toast';

export default function DashboardUsersPage() {
  const [users, setUsers] = useState<UserResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [roleFilter, setRoleFilter] = useState<string>('ALL');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    fetchUsers();
  }, [page, searchQuery, roleFilter, statusFilter]);

  const fetchUsers = async () => {
    try {
      setIsLoading(true);
      const response = await usersApi.getAllUsers({
        page,
        size: 10,
      });

      if (response.success) {
        setUsers(response.data.data);
        setTotalPages(response.data.totalPages);
      }
    } catch (error) {
      console.error('Failed to fetch users:', error);
      toast.error('Không thể tải danh sách người dùng');
    } finally {
      setIsLoading(false);
    }
  };

  const handleToggleStatus = async (userId: number, currentStatus: boolean) => {
    try {
      toast.success(currentStatus ? 'Đã vô hiệu hóa người dùng' : 'Đã kích hoạt người dùng');
      fetchUsers();
    } catch (error) {
      console.error('Failed to toggle user status:', error);
      toast.error('Không thể thay đổi trạng thái');
    }
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="animate-pulse space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="card p-6">
              <div className="flex gap-4">
                <div className="w-12 h-12 bg-gray-200 rounded-full" />
                <div className="flex-1 space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-1/3" />
                  <div className="h-4 bg-gray-200 rounded w-1/4" />
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  const userStats = {
    total: users.length,
    active: users.filter(u => u.isActive).length,
    inactive: users.filter(u => !u.isActive).length,
    admins: users.filter(u => u.role === 'ADMIN').length,
    customers: users.filter(u => u.role === 'CUSTOMER').length,
    verified: users.filter(u => u.isVerified).length,
  };

  return (
    <div className="container-custom py-12">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-[#1d1d1f] mb-2">
            Quản Lý Người Dùng
          </h1>
          <p className="text-[#6f6e72]">
            Quản lý tài khoản và phân quyền người dùng
          </p>
        </div>
        <Button size="lg" className="group">
          <UserPlus className="w-5 h-5 mr-2" />
          Thêm Người Dùng
        </Button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4 mb-8">
        {[
          { label: 'Tổng Số', value: userStats.total, color: 'text-gray-600', bgColor: 'bg-gray-100' },
          { label: 'Hoạt Động', value: userStats.active, color: 'text-green-600', bgColor: 'bg-green-100' },
          { label: 'Vô Hiệu', value: userStats.inactive, color: 'text-red-600', bgColor: 'bg-red-100' },
          { label: 'Admin', value: userStats.admins, color: 'text-purple-600', bgColor: 'bg-purple-100' },
          { label: 'Khách Hàng', value: userStats.customers, color: 'text-blue-600', bgColor: 'bg-blue-100' },
          { label: 'Đã Xác Thực', value: userStats.verified, color: 'text-green-600', bgColor: 'bg-green-100' },
        ].map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.05 }}
          >
            <Card>
              <CardContent className="p-4">
                <p className="text-xs text-[#6f6e72] mb-1">{stat.label}</p>
                <p className={`text-2xl font-bold ${stat.color}`}>{stat.value}</p>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>

      {/* Search and Filters */}
      <Card className="mb-6">
        <CardContent className="p-6">
          <div className="flex flex-col md:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-[#6f6e72]" />
                <input
                  type="text"
                  placeholder="Tìm kiếm người dùng (tên, email, username)..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-10 pr-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
                />
              </div>
            </div>
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
            >
              <option value="ALL">Tất cả vai trò</option>
              <option value="ADMIN">Admin</option>
              <option value="CUSTOMER">Khách hàng</option>
            </select>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-4 py-2 rounded-lg border border-gray-300 focus:outline-none focus:ring-2 focus:ring-[#1d1d1f]"
            >
              <option value="ALL">Tất cả trạng thái</option>
              <option value="ACTIVE">Hoạt động</option>
              <option value="INACTIVE">Vô hiệu</option>
            </select>
          </div>
        </CardContent>
      </Card>

      {/* Users Table */}
      <Card>
        <CardContent className="p-0">
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Người Dùng
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Liên Hệ
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Vai Trò
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Trạng Thái
                  </th>
                  <th className="px-6 py-4 text-left text-sm font-semibold text-[#1d1d1f]">
                    Ngày Tham Gia
                  </th>
                  <th className="px-6 py-4 text-right text-sm font-semibold text-[#1d1d1f]">
                    Thao Tác
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {users.map((user) => (
                  <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 rounded-full bg-[#1d1d1f] text-white flex items-center justify-center font-bold text-lg flex-shrink-0">
                          {user.firstName?.[0]}{user.lastName?.[0]}
                        </div>
                        <div>
                          <p className="font-semibold text-[#1d1d1f]">
                            {user.firstName} {user.lastName}
                          </p>
                          <p className="text-sm text-[#6f6e72]">
                            @{user.username}
                          </p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="space-y-1">
                        <div className="flex items-center gap-2 text-sm">
                          <Mail className="w-4 h-4 text-[#6f6e72]" />
                          <span className="text-[#1d1d1f]">{user.email}</span>
                        </div>
                        {user.phoneNumber && (
                          <p className="text-sm text-[#6f6e72]">
                            {user.phoneNumber}
                          </p>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <Badge variant={user.role === 'ADMIN' ? 'error' : 'info'}>
                        {user.role === 'ADMIN' ? 'Admin' : 'Khách hàng'}
                      </Badge>
                    </td>
                    <td className="px-6 py-4">
                      <div className="space-y-1">
                        <Badge variant={user.isActive ? 'success' : 'default'}>
                          {user.isActive ? 'Hoạt động' : 'Vô hiệu'}
                        </Badge>
                        {user.isVerified && (
                          <div className="flex items-center gap-1 text-xs text-green-600">
                            <CheckCircle className="w-3 h-3" />
                            <span>Đã xác thực</span>
                          </div>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2 text-sm text-[#6f6e72]">
                        <Calendar className="w-4 h-4" />
                        <span>{formatDate(user.createdAt)}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center justify-end gap-2">
                        <Button variant="ghost" size="sm">
                          <Eye className="w-4 h-4" />
                        </Button>
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => handleToggleStatus(user.id, user.isActive)}
                          className={user.isActive ? 'text-red-600' : 'text-green-600'}
                        >
                          {user.isActive ? (
                            <Ban className="w-4 h-4" />
                          ) : (
                            <CheckCircle className="w-4 h-4" />
                          )}
                        </Button>
                        {user.role !== 'ADMIN' && (
                          <Button variant="ghost" size="sm">
                            <Shield className="w-4 h-4" />
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {users.length === 0 && (
            <div className="text-center py-12">
              <Users className="w-16 h-16 mx-auto mb-4 text-[#6f6e72]" />
              <p className="text-[#6f6e72] text-lg">Chưa có người dùng nào</p>
            </div>
          )}
        </CardContent>
      </Card>

      {/* Pagination */}
      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      {/* Tips */}
      <Card className="mt-6">
        <CardContent className="p-6">
          <h3 className="font-bold text-[#1d1d1f] mb-4">💡 Mẹo Quản Lý Người Dùng</h3>
          <ul className="space-y-2 text-sm text-[#6f6e72]">
            <li>• Kiểm tra email xác thực trước khi cấp quyền admin</li>
            <li>• Vô hiệu hóa tài khoản thay vì xóa để giữ lịch sử đơn hàng</li>
            <li>• Theo dõi hoạt động của admin thường xuyên</li>
            <li>• Khuyến khích người dùng xác thực email để tăng bảo mật</li>
          </ul>
        </CardContent>
      </Card>
    </div>
  );
}
