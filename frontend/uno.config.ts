import { defineConfig, presetUno, presetAttributify, presetIcons } from 'unocss'

export default defineConfig({
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.2,
      warn: true,
    }),
  ],
  theme: {
    colors: {
      // Professional Blue Theme
      brand: {
        50: '#EFF6FF',
        100: '#DBEAFE',
        200: '#BFDBFE',
        300: '#93C5FD',
        400: '#60A5FA',
        500: '#3B82F6', // Primary
        600: '#2563EB',
        700: '#1D4ED8',
      },
      // Slate for text and backgrounds
      slate: {
        50: '#F8FAFC',
        100: '#F1F5F9',
        200: '#E2E8F0',
        300: '#CBD5E1',
        400: '#94A3B8',
        500: '#64748B',
        600: '#475569',
        700: '#334155',
        800: '#1E293B',
        900: '#0F172A',
      },
    },
  },
  shortcuts: {
    // Cards
    'card-base': 'bg-white rounded-xl border border-slate-200 shadow-sm',
    'card-hover': 'hover:shadow-md hover:border-brand-200 transition-all duration-200',
    // Buttons
    'btn-primary': 'bg-brand-500 text-white font-medium px-5 py-2.5 rounded-lg hover:bg-brand-600 transition-colors cursor-pointer',
    'btn-ghost': 'text-slate-600 hover:text-slate-900 hover:bg-slate-100 px-4 py-2 rounded-lg transition-colors cursor-pointer',
    // Layout
    'sidebar-item': 'flex items-center gap-3 px-4 py-2.5 rounded-lg text-slate-600 hover:bg-slate-100 hover:text-slate-900 transition-all cursor-pointer',
    'sidebar-item-active': 'bg-brand-50 text-brand-600 font-medium',
  },
  safelist: [
    'i-solar-home-smile-linear',
    'i-solar-info-circle-linear',
  ],
})
