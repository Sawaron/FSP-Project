const demoRating = [
{ rank: 1, name: 'Иванов Иван', points: 342, qualification: 'КМС' },
{ rank: 2, name: 'Петров Пётр', points: 298, qualification: 'I разряд' },
{ rank: 3, name: 'Сидорова Анна', points: 271, qualification: 'КМС' },
];

function RatingPage() {
return (
<div className="page">
  <div className="page-header">
    <h1>Рейтинг спортсменов</h1>
    <p>Внутренний рейтинг Федерации на основе результатов и квалификации</p>
  </div>

  <div className="table-wrap">
    <table>
      <thead>
      <tr>
        <th>Место</th>
        <th>Спортсмен</th>
        <th>Квалификация</th>
        <th>Баллы</th>
      </tr>
      </thead>
      <tbody>
      {demoRating.map(r => (
      <tr key={r.rank}>
        <td className="rank-cell">#{r.rank}</td>
        <td>{r.name}</td>
        <td>{r.qualification}</td>
        <td>{r.points}</td>
      </tr>
      ))}
      </tbody>
    </table>
  </div>
</div>
);
}

export default RatingPage;