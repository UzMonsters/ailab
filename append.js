const fs = require('fs');
const css = `
@keyframes rise { 0% { transform: translateY(0) scale(0.5); opacity: 0; } 20% { opacity: 1; } 80% { opacity: 1; } 100% { transform: translateY(-40px) scale(1.2); opacity: 0; } }
.animate-rise { animation: rise 1.5s ease-in infinite; }
.delay-100 { animation-delay: 0.1s; }
.delay-300 { animation-delay: 0.3s; }
.delay-500 { animation-delay: 0.5s; }
.delay-700 { animation-delay: 0.7s; }

@keyframes dash-flow { to { stroke-dashoffset: -20; } }
.animate-dash-flow { animation: dash-flow 0.5s linear infinite; }

@keyframes steam-rise { 0% { transform: translateY(0) scale(0.8); opacity: 0; } 20% { opacity: 0.6; } 100% { transform: translateY(-30px) scale(1.5); opacity: 0; } }
.animate-steam-rise { animation: steam-rise 2s ease-out infinite; }
`;
fs.appendFileSync('D:/web-sites/заказ/aichemistry/frontend/src/app/globals.css', css, 'utf8');
