const fs = require('fs');
const uz = JSON.parse(fs.readFileSync('src/messages/uz.json', 'utf8'));
uz.academy = {
  title: 'jasScience — The Scientific OS',
  subtitle: "Interaktiv kimyo akademiyasi. Haqiqiy tadqiqotchi bo'lish uchun darajalarni yakunlang!",
  levels: {
    l1: {
      title: 'Laboratoriya asoslari',
      desc: "Kimyoviy idishlar bilan tanishish, ish stolidagi obyektlarni to'g'ri harakatlantirish va asosiy xavfsizlik qoidalari."
    },
    l2: {
      title: "O'lchash va qizdirish",
      desc: "Bunsen gorelkalari va plitkalardan foydalanish, termometrni ulash va suyuqlik haroratini aniq o'lchash."
    },
    l3: {
      title: 'Reaksiyalar va suyuqlik xossalari',
      desc: "Suyuqliklarni quyish, eritmalarni aralashtirish va kislota-ishqor neytrallashish kabi kimyoviy reaksiyalarni vizuallashtirish."
    },
    l4: {
      title: 'Termodinamika va gazlar',
      desc: "Bosim bilan ishlash, yopiq kolbalarda gazlarni ushlab turish, faza o'tishlari va kondensatsiya."
    }
  }
};
fs.writeFileSync('src/messages/uz.json', JSON.stringify(uz, null, 2));
