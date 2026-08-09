src/
│
├── app/
│   │
│   ├── layout.tsx
│   ├── globals.css
│   ├── not-found.tsx
│   ├── error.tsx
│   │
│   ├── (public)/
│   │   ├── layout.tsx
│   │   │
│   │   ├── page.tsx
│   │   │
│   │   ├── about/
│   │   │   └── page.tsx
│   │   │
│   │   └── terms/
│   │       └── page.tsx
│   │
│   ├── (user)/
│   │   ├── layout.tsx
│   │   │
│   │   ├── dashboard/
│   │   │   └── page.tsx
│   │   │
│   │   ├── profile/
│   │   │   └── page.tsx
│   │   │
│   │   └── workspace/
│   │       └── sandbox/
│   │           └── page.tsx
│   │
│   └── admin/
│       ├── layout.tsx
│       ├── page.tsx
│       │
│       ├── laboratories/
│       │   ├── page.tsx
│       │   └── [id]/
│       │       └── page.tsx
│       │
│       ├── users/
│       │   ├── page.tsx
│       │   └── [id]/
│       │       └── page.tsx
│       │
│       ├── chemicals/
│       │   └── page.tsx
│       │
│       ├── elements/
│       │   └── page.tsx
│       │
│       └── equipment/
│           └── page.tsx
│
│
├── views/
│   │
│   ├── landing/
│   │   ├── LandingPage.tsx
│   │   │
│   │   └── sections/
│   │       ├── HeroSection.tsx
│   │       ├── PlatformSection.tsx
│   │       ├── SciencesSection.tsx
│   │       ├── SandboxSection.tsx
│   │       ├── AIAssistantSection.tsx
│   │       ├── FeaturesSection.tsx
│   │       ├── CTASection.tsx
│   │       └── FooterSection.tsx
│   │
│   ├── about/
│   │   ├── AboutPage.tsx
│   │   └── sections/
│   │       ├── AboutHeroSection.tsx
│   │       ├── MissionSection.tsx
│   │       ├── VisionSection.tsx
│   │       ├── PlatformSection.tsx
│   │       └── TeamSection.tsx
│   │
│   ├── dashboard/
│   │   ├── DashboardPage.tsx
│   │   └── sections/
│   │       ├── DashboardHeader.tsx
│   │       ├── ContinueLearningSection.tsx
│   │       ├── LaboratoriesSection.tsx
│   │       ├── RecentActivitySection.tsx
│   │       └── SciencesSection.tsx
│   │
│   ├── profile/
│   │   ├── ProfilePage.tsx
│   │   └── sections/
│   │       ├── ProfileHeader.tsx
│   │       ├── PersonalInfoSection.tsx
│   │       ├── StatisticsSection.tsx
│   │       ├── ActivitySection.tsx
│   │       └── PreferencesSection.tsx
│   │
│   ├── workspace/
│   │   └── sandbox/
│   │       ├── SandboxPage.tsx
│   │       │
│   │       ├── sections/
│   │       │   ├── SandboxHeader.tsx
│   │       │   ├── ToolsPanel.tsx
│   │       │   ├── LibraryPanel.tsx
│   │       │   ├── CanvasSection.tsx
│   │       │   ├── PropertiesPanel.tsx
│   │       │   └── BottomPanel.tsx
│   │       │
│   │       └── components/
│   │           ├── EquipmentItem.tsx
│   │           ├── ChemicalItem.tsx
│   │           ├── CanvasObject.tsx
│   │           ├── Connection.tsx
│   │           └── SimulationControls.tsx
│   │
│   ├── admin/
│   │   │
│   │   ├── dashboard/
│   │   │   ├── AdminDashboardPage.tsx
│   │   │   └── sections/
│   │   │       ├── StatisticsSection.tsx
│   │   │       ├── RecentUsersSection.tsx
│   │   │       ├── RecentLabsSection.tsx
│   │   │       └── SystemStatusSection.tsx
│   │   │
│   │   ├── laboratories/
│   │   │   ├── LaboratoriesPage.tsx
│   │   │   ├── sections/
│   │   │   └── components/
│   │   │
│   │   ├── users/
│   │   │   ├── UsersPage.tsx
│   │   │   ├── sections/
│   │   │   └── components/
│   │   │
│   │   ├── chemicals/
│   │   ├── elements/
│   │   └── equipment/
│   │
│   ├── terms/
│   │   ├── TermsPage.tsx
│   │   └── sections/
│   │       ├── TermsHero.tsx
│   │       └── TermsContent.tsx
│   │
│   └── not-found/
│       └── NotFoundPage.tsx
│
│
├── components/
│   │
│   ├── ui/
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Select.tsx
│   │   ├── Tabs.tsx
│   │   ├── Tooltip.tsx
│   │   ├── Card.tsx
│   │   └── Badge.tsx
│   │
│   ├── layout/
│   │   ├── PublicHeader.tsx
│   │   ├── PublicFooter.tsx
│   │   ├── UserHeader.tsx
│   │   ├── UserSidebar.tsx
│   │   ├── AdminHeader.tsx
│   │   └── AdminSidebar.tsx
│   │
│   └── common/
│       ├── Logo.tsx
│       ├── Avatar.tsx
│       ├── LoadingScreen.tsx
│       ├── EmptyState.tsx
│       └── PageTitle.tsx
│
│
├── features/
│   ├── auth/
│   ├── theme/
│   ├── laboratory/
│   ├── simulation/
│   └── profile/
│
│
├── services/
│   ├── api/
│   │   ├── client.ts
│   │   ├── auth.api.ts
│   │   ├── user.api.ts
│   │   ├── laboratory.api.ts
│   │   ├── chemistry.api.ts
│   │   └── admin.api.ts
│   │
│   └── websocket/
│       └── workspace.socket.ts
│
├── stores/
│   ├── auth.store.ts
│   ├── user.store.ts
│   ├── sandbox.store.ts
│   └── ui.store.ts
│
├── hooks/
│
├── lib/
│
├── types/
│
├── constants/
│
└── assets/