'use client';

import React from 'react';
import Link from 'next/link';
import { Facebook, Instagram, Twitter, Mail, Phone, MapPin } from 'lucide-react';

export const Footer: React.FC = () => {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="bg-white border-t border-gray-200 mt-20">
      <div className="container-custom py-12">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {/* Brand */}
          <div>
            <h3 className="text-2xl font-bold text-main-text mb-4">ShoeStore</h3>
            <p className="text-semi-text mb-4">
              Your premier destination for quality footwear. Step into style with our curated collection.
            </p>
            <div className="flex space-x-4">
              <a href="#" className="text-semi-text hover:text-main-text transition-colors">
                <Facebook className="w-5 h-5" />
              </a>
              <a href="#" className="text-semi-text hover:text-main-text transition-colors">
                <Instagram className="w-5 h-5" />
              </a>
              <a href="#" className="text-semi-text hover:text-main-text transition-colors">
                <Twitter className="w-5 h-5" />
              </a>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 className="font-bold text-main-text mb-4">Quick Links</h4>
            <ul className="space-y-2">
              <li>
                <Link href="/products" className="text-semi-text hover:text-main-text transition-colors">
                  Shop All
                </Link>
              </li>
              <li>
                <Link href="/categories" className="text-semi-text hover:text-main-text transition-colors">
                  Categories
                </Link>
              </li>
              <li>
                <Link href="/about" className="text-semi-text hover:text-main-text transition-colors">
                  About Us
                </Link>
              </li>
              <li>
                <Link href="/contact" className="text-semi-text hover:text-main-text transition-colors">
                  Contact
                </Link>
              </li>
            </ul>
          </div>

          {/* Customer Service */}
          <div>
            <h4 className="font-bold text-main-text mb-4">Customer Service</h4>
            <ul className="space-y-2">
              <li>
                <Link href="/faq" className="text-semi-text hover:text-main-text transition-colors">
                  FAQ
                </Link>
              </li>
              <li>
                <Link href="/shipping" className="text-semi-text hover:text-main-text transition-colors">
                  Shipping Info
                </Link>
              </li>
              <li>
                <Link href="/returns" className="text-semi-text hover:text-main-text transition-colors">
                  Returns
                </Link>
              </li>
              <li>
                <Link href="/privacy" className="text-semi-text hover:text-main-text transition-colors">
                  Privacy Policy
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h4 className="font-bold text-main-text mb-4">Contact Us</h4>
            <ul className="space-y-3">
              <li className="flex items-start space-x-3">
                <MapPin className="w-5 h-5 text-semi-text flex-shrink-0 mt-0.5" />
                <span className="text-semi-text">123 Shoe Street, Fashion District, City</span>
              </li>
              <li className="flex items-center space-x-3">
                <Phone className="w-5 h-5 text-semi-text flex-shrink-0" />
                <span className="text-semi-text">+1 234 567 890</span>
              </li>
              <li className="flex items-center space-x-3">
                <Mail className="w-5 h-5 text-semi-text flex-shrink-0" />
                <span className="text-semi-text">support@shoestore.com</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Bottom Bar */}
        <div className="border-t border-gray-200 mt-8 pt-8 text-center">
          <p className="text-semi-text text-sm">
            © {currentYear} ShoeStore. All rights reserved.
          </p>
        </div>
      </div>
    </footer>
  );
};
