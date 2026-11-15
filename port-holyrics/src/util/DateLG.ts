/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.util.DateLG
 */
export class DateLG {
  public static equalDays(d1: Date, d2: Date): boolean {
    return (
      d1.getFullYear() === d2.getFullYear() &&
      d1.getMonth() === d2.getMonth() &&
      d1.getDate() === d2.getDate()
    );
  }

  public static isToday(date: Date): boolean {
    return DateLG.equalDays(date, new Date());
  }

  public static isWeekend(date: Date): boolean {
    const day = date.getDay();
    return day === 0 || day === 6;
  }

  public static isBetween(date: Date, start: Date, end: Date): boolean {
    const t = date.getTime();
    return start.getTime() <= end.getTime() && t >= start.getTime() && t <= end.getTime();
  }
}

export default DateLG;
