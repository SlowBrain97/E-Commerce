'use client';

import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Phone, Calendar, Edit2, Save, X } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { Input } from '@/components/ui/Input';
import { Card, CardHeader, CardContent } from '@/components/ui/Card';
import { useAuthStore } from '@/lib/store/authStore';
import { usersApi } from '@/lib/api/users';
import { UserResponse } from '@/lib/types/api';
import { formatDate } from '@/lib/utils/format';
import { toast } from 'react-hot-toast';

export default function ProfilePage() {
  const { user } = useAuthStore();
  const [profile, setProfile] = useState<UserResponse | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const response = await usersApi.getCurrentUserProfile();
      if (response.success) {
        setProfile(response.data);
        setFormData({
          firstName: response.data.firstName || '',
          lastName: response.data.lastName || '',
          email: response.data.email || '',
          phoneNumber: response.data.phoneNumber || '',
        });
      }
    } catch (error) {
      console.error('Failed to fetch profile:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    try {
      const response = await usersApi.updateProfile(formData);
      if (response.success) {
        setProfile(response.data);
        setIsEditing(false);
        toast.success('Profile updated successfully');
      }
    } catch (error) {
      console.error('Failed to update profile:', error);
    }
  };

  const handleCancel = () => {
    if (profile) {
      setFormData({
        firstName: profile.firstName || '',
        lastName: profile.lastName || '',
        email: profile.email || '',
        phoneNumber: profile.phoneNumber || '',
      });
    }
    setIsEditing(false);
  };

  if (isLoading) {
    return (
      <div className="container-custom py-12">
        <div className="max-w-2xl mx-auto">
          <div className="card p-8 animate-pulse">
            <div className="h-8 bg-gray-200 rounded w-1/3 mb-6" />
            <div className="space-y-4">
              {[...Array(4)].map((_, i) => (
                <div key={i} className="h-12 bg-gray-200 rounded" />
              ))}
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="container-custom py-12 text-center">
        <p className="text-semi-text">Failed to load profile</p>
      </div>
    );
  }

  return (
    <div className="container-custom py-12">
      <div className="max-w-2xl mx-auto">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className="flex items-center justify-between mb-8">
            <div>
              <h1 className="text-3xl font-bold text-main-text mb-2">My Profile</h1>
              <p className="text-semi-text">Manage your account information</p>
            </div>
            {!isEditing && (
              <Button variant="outline" onClick={() => setIsEditing(true)}>
                <Edit2 className="w-4 h-4 mr-2" />
                Edit Profile
              </Button>
            )}
          </div>

          <Card>
            <CardHeader>
              <div className="flex items-center gap-4">
                <div className="w-20 h-20 rounded-full bg-main-text text-white flex items-center justify-center text-2xl font-bold">
                  {profile.firstName?.[0]}{profile.lastName?.[0]}
                </div>
                <div>
                  <h2 className="text-xl font-bold text-main-text">
                    {profile.firstName} {profile.lastName}
                  </h2>
                  <p className="text-semi-text">@{profile.username}</p>
                </div>
              </div>
            </CardHeader>

            <CardContent>
              {isEditing ? (
                <form onSubmit={handleSubmit} className="space-y-6">
                  <div className="grid grid-cols-2 gap-4">
                    <Input
                      label="First Name"
                      value={formData.firstName}
                      onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                    />
                    <Input
                      label="Last Name"
                      value={formData.lastName}
                      onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                    />
                  </div>

                  <Input
                    label="Email"
                    type="email"
                    icon={<Mail className="w-5 h-5" />}
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  />

                  <Input
                    label="Phone Number"
                    type="tel"
                    icon={<Phone className="w-5 h-5" />}
                    value={formData.phoneNumber}
                    onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                  />

                  <div className="flex gap-4">
                    <Button type="submit" className="flex-1">
                      <Save className="w-4 h-4 mr-2" />
                      Save Changes
                    </Button>
                    <Button type="button" variant="outline" onClick={handleCancel}>
                      <X className="w-4 h-4 mr-2" />
                      Cancel
                    </Button>
                  </div>
                </form>
              ) : (
                <div className="space-y-6">
                  <div className="grid grid-cols-2 gap-6">
                    <div>
                      <label className="text-sm text-semi-text mb-1 block">First Name</label>
                      <p className="font-semibold text-main-text">{profile.firstName || '-'}</p>
                    </div>
                    <div>
                      <label className="text-sm text-semi-text mb-1 block">Last Name</label>
                      <p className="font-semibold text-main-text">{profile.lastName || '-'}</p>
                    </div>
                  </div>

                  <div>
                    <label className="text-sm text-semi-text mb-1 block flex items-center gap-2">
                      <Mail className="w-4 h-4" />
                      Email
                    </label>
                    <p className="font-semibold text-main-text">{profile.email}</p>
                  </div>

                  <div>
                    <label className="text-sm text-semi-text mb-1 block flex items-center gap-2">
                      <Phone className="w-4 h-4" />
                      Phone Number
                    </label>
                    <p className="font-semibold text-main-text">{profile.phoneNumber || '-'}</p>
                  </div>

                  <div>
                    <label className="text-sm text-semi-text mb-1 block flex items-center gap-2">
                      <User className="w-4 h-4" />
                      Username
                    </label>
                    <p className="font-semibold text-main-text">{profile.username}</p>
                  </div>

                  <div>
                    <label className="text-sm text-semi-text mb-1 block flex items-center gap-2">
                      <Calendar className="w-4 h-4" />
                      Member Since
                    </label>
                    <p className="font-semibold text-main-text">
                      {formatDate(profile.createdAt)}
                    </p>
                  </div>

                  <div className="pt-6 border-t border-gray-200">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-sm text-semi-text">Account Status</p>
                        <p className="font-semibold text-main-text">
                          {profile.isActive ? 'Active' : 'Inactive'}
                        </p>
                      </div>
                      <div>
                        <p className="text-sm text-semi-text">Email Verified</p>
                        <p className="font-semibold text-main-text">
                          {profile.isVerified ? 'Yes' : 'No'}
                        </p>
                      </div>
                      <div>
                        <p className="text-sm text-semi-text">Role</p>
                        <p className="font-semibold text-main-text capitalize">
                          {profile.role.toLowerCase()}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>

          {/* Additional Sections */}
          <div className="grid md:grid-cols-2 gap-6 mt-6">
            <Card>
              <CardHeader>
                <h3 className="font-bold text-main-text">Security</h3>
              </CardHeader>
              <CardContent>
                <Button variant="outline" className="w-full">
                  Change Password
                </Button>
              </CardContent>
            </Card>

            <Card>
              <CardHeader>
                <h3 className="font-bold text-main-text">Preferences</h3>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  <label className="flex items-center justify-between">
                    <span className="text-semi-text">Email Notifications</span>
                    <input type="checkbox" className="rounded border-gray-300" defaultChecked />
                  </label>
                  <label className="flex items-center justify-between">
                    <span className="text-semi-text">Order Updates</span>
                    <input type="checkbox" className="rounded border-gray-300" defaultChecked />
                  </label>
                  <label className="flex items-center justify-between">
                    <span className="text-semi-text">Promotional Emails</span>
                    <input type="checkbox" className="rounded border-gray-300" />
                  </label>
                </div>
              </CardContent>
            </Card>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
