import createNextIntlPlugin from 'next-intl/plugin';

const withNextIntl = createNextIntlPlugin('./src/i18n/request.ts');

const nextConfig = {
  output: 'standalone',

  images: {
    unoptimized: true,
  },

};

export default withNextIntl(nextConfig);
