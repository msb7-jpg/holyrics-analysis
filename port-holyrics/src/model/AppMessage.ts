function escapeHTML(str: string): string {
  return (str || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;');
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.AppMessage
 */
export class AppMessage {
  public static readonly serialVersionUID = 1;
  private static LAST_DATETIME: number = 0;
  public static readonly ADMIN_ID: string = 'holyrics-admin';

  public readonly datetime: number;
  public readonly userId: string;
  public readonly name: string;
  public readonly message: string;

  private constructor(datetime: number, userId: string, name: string, message: string) {
    this.datetime = datetime;
    this.userId = userId;
    this.name = (name || '').replace(/\n/g, ' ');
    this.message = message;
  }

  public static create(userId: string, name: string, message: string): AppMessage {
    let datetime = Date.now();
    if (datetime <= AppMessage.LAST_DATETIME) {
      datetime = ++AppMessage.LAST_DATETIME;
    } else {
      AppMessage.LAST_DATETIME = datetime;
    }
    return new AppMessage(datetime, userId, name, message);
  }

  public toString(): string {
    return `${this.name}\n${this.message}`;
  }

  public toStringHTML(): string {
    return `<html><b>${escapeHTML(this.name)}:</b><br>${escapeHTML(this.message)}`;
  }

  public checkUser(userId: string): boolean {
    return this.userId === userId;
  }

  public isMyself(): boolean {
    return this.checkUser(AppMessage.ADMIN_ID);
  }
}

export default AppMessage;
