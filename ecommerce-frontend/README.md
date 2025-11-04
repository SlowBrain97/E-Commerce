# 🛍️ Eコマース フロントエンド

Next.js 16とReact 19を使用して構築されたモダンなEコマースプラットフォームのフロントエンドアプリケーションです。

## 📋 目次

- [概要](#概要)
- [技術スタック](#技術スタック)
- [機能](#機能)
- [前提条件](#前提条件)
- [インストール](#インストール)
- [環境変数の設定](#環境変数の設定)
- [起動方法](#起動方法)
- [プロジェクト構成](#プロジェクト構成)
- [開発](#開発)
- [ビルド](#ビルド)
- [デプロイ](#デプロイ)

## 🎯 概要

このプロジェクトは、最新のNext.js 16とReact 19を使用して構築された、モダンでレスポンシブなEコマースアプリケーションのフロントエンドです。TailwindCSSによる美しいUI、Zustandによる効率的な状態管理、そしてスムーズなアニメーションを特徴としています。

### 主な特徴

- ⚡ Next.js 16のApp Routerを使用した高速なページ遷移
- 🎨 TailwindCSSによるモダンでレスポンシブなデザイン
- 🔄 Zustandによる軽量で効率的な状態管理
- 📱 完全レスポンシブ対応（モバイル、タブレット、デスクトップ）
- 🎭 Framer Motionによる滑らかなアニメーション
- 🔐 JWT認証とGoogle OAuth 2.0統合
- 💳 Stripe決済統合
- 🚀 TypeScriptによる型安全な開発
- 🎯 React Hook Formによる効率的なフォーム管理
- 🔔 React Hot Toastによる通知システム

## 🛠 技術スタック

### コアフレームワーク

- **Next.js**: 16.0.0 - Reactベースのフルスタックフレームワーク
- **React**: 19.2.0 - UIライブラリ
- **TypeScript**: 5.x - 型安全なJavaScript

### UIとスタイリング

- **TailwindCSS**: 3.4.18 - ユーティリティファーストCSSフレームワーク
- **Lucide React**: 0.546.0 - モダンなアイコンライブラリ
- **Framer Motion**: 11.0.0 - アニメーションライブラリ
- **Swiper**: 11.0.0 - モダンなスライダー/カルーセル

### 状態管理とフォーム

- **Zustand**: 4.5.0 - 軽量な状態管理ライブラリ
- **React Hook Form**: 7.50.0 - 高性能なフォームライブラリ
- **Zod**: 3.22.0 - TypeScriptファーストのスキーマバリデーション
- **@hookform/resolvers**: 3.3.0 - React Hook FormとZodの統合

### HTTPクライアントとユーティリティ

- **Axios**: 1.6.0 - HTTPクライアント
- **date-fns**: 3.3.0 - 日付操作ライブラリ
- **clsx**: 2.1.0 - クラス名の条件付き結合
- **tailwind-merge**: 2.2.0 - Tailwindクラスのマージ

### 通知とUI機能

- **React Hot Toast**: 2.4.0 - トースト通知
- **React Intersection Observer**: 9.8.0 - 要素の可視性検出

### 開発ツール

- **ESLint**: 9.x - コード品質チェック
- **PostCSS**: 8.5.6 - CSS処理
- **Autoprefixer**: 10.4.21 - CSSベンダープレフィックス自動付与

## ✨ 機能

### ユーザー向け機能

- 🔐 **認証システム**
  - ユーザー登録・ログイン
  - Google OAuth認証
  - パスワードリセット
  - セッション管理

- 🛍️ **ショッピング機能**
  - 商品一覧表示
  - 商品詳細ページ
  - カテゴリ別フィルタリング
  - 商品検索
  - カート管理
  - お気に入り機能

- 💳 **決済機能**
  - Stripe決済統合
  - 安全な決済処理
  - 注文確認
  - 注文履歴

- 👤 **ユーザープロフィール**
  - プロフィール編集
  - 配送先住所管理
  - 注文履歴表示
  - アカウント設定

### UI/UX機能

- 📱 レスポンシブデザイン
- 🎨 ダークモード対応（オプション）
- ⚡ 高速なページ遷移
- 🎭 滑らかなアニメーション
- 🔔 リアルタイム通知
- ♿ アクセシビリティ対応

## 📦 前提条件

開発環境に以下がインストールされている必要があります：

- **Node.js**: 20.x以上
- **npm**: 10.x以上（またはyarn、pnpm）
- **Git**: 最新版

## 🚀 インストール

### 1. リポジトリのクローン

```bash
git clone https://github.com/yourusername/E-Commerce.git
cd E-Commerce/ecommerce-frontend
```

### 2. 依存関係のインストール

```bash
cd src
npm install
```

または

```bash
cd src
yarn install
```

## 🔧 環境変数の設定

プロジェクトルートに `.env.local` ファイルを作成します：

```bash
cp .env.example .env.local
```

`.env.local` ファイルを編集して、以下の環境変数を設定します：

```env
# バックエンドAPI URL
NEXT_PUBLIC_API_URL=http://localhost:8080

# NextAuth設定（認証に必要）
NEXTAUTH_URL=http://localhost:3000
NEXTAUTH_SECRET=your-secret-key-for-nextauth

# Google OAuth設定
GOOGLE_CLIENT_ID=your-google-oauth-client-id
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret

# Stripe公開鍵
NEXT_PUBLIC_STRIPE_PUBLIC_KEY=pk_test_your_stripe_public_key
```

### 環境変数の説明

| 変数名 | 説明 | 必須 |
|--------|------|------|
| `NEXT_PUBLIC_API_URL` | バックエンドAPIのベースURL | ✅ |
| `NEXTAUTH_URL` | アプリケーションのベースURL | ✅ |
| `NEXTAUTH_SECRET` | NextAuthのシークレットキー | ✅ |
| `GOOGLE_CLIENT_ID` | Google OAuthクライアントID | ⚠️ |
| `GOOGLE_CLIENT_SECRET` | Google OAuthクライアントシークレット | ⚠️ |
| `NEXT_PUBLIC_STRIPE_PUBLIC_KEY` | Stripeの公開鍵 | ⚠️ |

⚠️ = OAuth/決済機能を使用する場合に必要

## 🏃 起動方法

### 開発モード

```bash
cd src
npm run dev
```

アプリケーションは http://localhost:3000 で起動します。

### 本番ビルド

```bash
cd src
npm run build
npm run start
```

### Dockerを使用した起動

プロジェクトルートから：

```bash
docker-compose up -d frontend
```

## 📁 プロジェクト構成

```
ecommerce-frontend/
├── src/
│   ├── app/                      # Next.js App Router
│   │   ├── (auth)/              # 認証関連ページ
│   │   │   ├── login/           # ログインページ
│   │   │   ├── register/        # 登録ページ
│   │   │   └── reset-password/  # パスワードリセット
│   │   ├── (shop)/              # ショップページ
│   │   │   ├── products/        # 商品一覧
│   │   │   ├── product/[id]/    # 商品詳細
│   │   │   ├── cart/            # カート
│   │   │   └── checkout/        # チェックアウト
│   │   ├── (user)/              # ユーザーページ
│   │   │   ├── profile/         # プロフィール
│   │   │   ├── orders/          # 注文履歴
│   │   │   └── settings/        # 設定
│   │   ├── api/                 # APIルート
│   │   ├── layout.tsx           # ルートレイアウト
│   │   └── page.tsx             # ホームページ
│   │
│   ├── components/              # Reactコンポーネント
│   │   ├── ui/                  # 再利用可能なUIコンポーネント
│   │   │   ├── Button.tsx
│   │   │   ├── Input.tsx
│   │   │   ├── Card.tsx
│   │   │   └── Modal.tsx
│   │   ├── layout/              # レイアウトコンポーネント
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   └── Sidebar.tsx
│   │   ├── product/             # 商品関連コンポーネント
│   │   │   ├── ProductCard.tsx
│   │   │   ├── ProductList.tsx
│   │   │   └── ProductFilter.tsx
│   │   └── cart/                # カート関連コンポーネント
│   │       ├── CartItem.tsx
│   │       └── CartSummary.tsx
│   │
│   ├── lib/                     # ユーティリティ関数
│   │   ├── api.ts               # APIクライアント
│   │   ├── auth.ts              # 認証ヘルパー
│   │   ├── utils.ts             # 汎用ユーティリティ
│   │   └── validators.ts        # バリデーション関数
│   │
│   ├── hooks/                   # カスタムReactフック
│   │   ├── useAuth.ts           # 認証フック
│   │   ├── useCart.ts           # カートフック
│   │   ├── useProducts.ts       # 商品フック
│   │   └── useOrders.ts         # 注文フック
│   │
│   ├── store/                   # Zustand状態管理
│   │   ├── authStore.ts         # 認証状態
│   │   ├── cartStore.ts         # カート状態
│   │   └── uiStore.ts           # UI状態
│   │
│   ├── types/                   # TypeScript型定義
│   │   ├── product.ts
│   │   ├── user.ts
│   │   ├── order.ts
│   │   └── cart.ts
│   │
│   ├── styles/                  # グローバルスタイル
│   │   └── globals.css
│   │
│   ├── public/                  # 静的ファイル
│   │   ├── images/
│   │   └── icons/
│   │
│   ├── package.json             # プロジェクト依存関係
│   ├── tsconfig.json            # TypeScript設定
│   ├── tailwind.config.js       # TailwindCSS設定
│   ├── postcss.config.js        # PostCSS設定
│   └── next.config.js           # Next.js設定
│
├── .env.example                 # 環境変数のサンプル
├── Dockerfile                   # Docker設定
└── README.md                    # このファイル
```

## 💻 開発

### 開発サーバーの起動

```bash
cd src
npm run dev
```

開発サーバーは http://localhost:3000 で起動します。
ファイルを編集すると、自動的にページがリロードされます。

### コードの品質チェック

```bash
# ESLintでコードをチェック
npm run lint

# 型チェック
npx tsc --noEmit
```

### コーディング規約

- **コンポーネント**: PascalCaseを使用（例: `ProductCard.tsx`）
- **関数/変数**: camelCaseを使用（例: `getUserData`）
- **定数**: UPPER_SNAKE_CASEを使用（例: `API_BASE_URL`）
- **ファイル名**: コンポーネントはPascalCase、その他はkebab-case
- **インポート順序**: 
  1. Reactとサードパーティライブラリ
  2. 内部モジュール
  3. 相対パス
  4. スタイル

### 推奨VSCode拡張機能

- ESLint
- Prettier
- Tailwind CSS IntelliSense
- TypeScript Vue Plugin (Volar)
- Auto Rename Tag
- Path Intellisense

## 🏗️ ビルド

### 本番ビルドの作成

```bash
cd src
npm run build
```

ビルドされたファイルは `.next` ディレクトリに生成されます。

### 本番モードでの起動

```bash
npm run start
```

### ビルドの最適化

Next.jsは自動的に以下の最適化を行います：

- コード分割
- 画像最適化
- フォント最適化
- 静的サイト生成（SSG）
- サーバーサイドレンダリング（SSR）
- インクリメンタル静的再生成（ISR）

## 🚢 デプロイ

### Vercelへのデプロイ（推奨）

1. [Vercel](https://vercel.com)にサインアップ
2. GitHubリポジトリを接続
3. 環境変数を設定
4. デプロイ

```bash
# Vercel CLIを使用
npm i -g vercel
vercel
```

### Dockerを使用したデプロイ

```bash
# イメージをビルド
docker build -t ecommerce-frontend .

# コンテナを起動
docker run -p 3000:3000 ecommerce-frontend
```

### 環境変数の設定

本番環境では、以下の環境変数を必ず設定してください：

- `NEXT_PUBLIC_API_URL`: 本番バックエンドURL
- `NEXTAUTH_URL`: 本番フロントエンドURL
- `NEXTAUTH_SECRET`: 強力なシークレットキー
- その他の認証・決済関連の環境変数

## 🔧 トラブルシューティング

### よくある問題

#### 1. ポート3000が既に使用されている

```bash
# 別のポートで起動
PORT=3001 npm run dev
```

#### 2. 依存関係のインストールエラー

```bash
# node_modulesとpackage-lock.jsonを削除して再インストール
rm -rf node_modules package-lock.json
npm install
```

#### 3. ビルドエラー

```bash
# キャッシュをクリア
rm -rf .next
npm run build
```

#### 4. 環境変数が読み込まれない

- `.env.local` ファイルが正しい場所にあるか確認
- 開発サーバーを再起動
- `NEXT_PUBLIC_` プレフィックスが必要な変数に付いているか確認

## 🧪 テスト

```bash
# テストの実行（将来的に追加予定）
npm test

# テストカバレッジ
npm run test:coverage
```

## 📝 スクリプト一覧

| コマンド | 説明 |
|---------|------|
| `npm run dev` | 開発サーバーを起動 |
| `npm run build` | 本番用ビルドを作成 |
| `npm run start` | 本番モードで起動 |
| `npm run lint` | ESLintでコードをチェック |

## 🤝 コントリビューション

1. このリポジトリをフォーク
2. フィーチャーブランチを作成 (`git checkout -b feature/AmazingFeature`)
3. 変更をコミット (`git commit -m 'Add some AmazingFeature'`)
4. ブランチにプッシュ (`git push origin feature/AmazingFeature`)
5. プルリクエストを作成

## 📄 ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## 🔗 関連リンク

- [Next.js ドキュメント](https://nextjs.org/docs)
- [React ドキュメント](https://react.dev/)
- [TailwindCSS ドキュメント](https://tailwindcss.com/docs)
- [TypeScript ドキュメント](https://www.typescriptlang.org/docs/)
- [Zustand ドキュメント](https://zustand-demo.pmnd.rs/)

## 📞 サポート

問題が発生した場合は、[Issue](https://github.com/yourusername/E-Commerce/issues)を作成してください。

---

⭐ このプロジェクトが役に立った場合は、スターをつけていただけると嬉しいです！
