/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Account
 */

export class Account {
  public static readonly serialVersionUID = 12491;

  private username: string;
  private accessKey: string;
  private security: string;
  private version: number = 1;

  constructor(username: string = "", accessKey: string = "", security: string = "") {
    this.username = username;
    this.accessKey = accessKey;
    this.security = security;
  }

  public getUsername(): string {
    return this.username;
  }

  public setUsername(username: string): void {
    this.username = username;
  }

  public getAccessKey(): string {
    return this.accessKey;
  }

  public setAccessKey(accessKey: string): void {
    this.accessKey = accessKey;
  }

  public getSecurity(): string {
    return this.security;
  }

  public setSecurity(security: string): void {
    this.security = security;
  }

  public getVersion(): number {
    return this.version;
  }

  public setVersion(version: number): void {
    this.version = version;
  }

  public validate(): void {
    if (this.username.length < 4) {
      throw new Error("Nome de usuário inválido.\nQuantidade mínima de caracteres = 4");
    } else if (this.accessKey.length < 4 || this.security.length < 4) {
      throw new Error("Senha inválida.\nQuantidade mínima de caracteres = 4");
    }
  }

  public clear(): void {
    this.username = "";
    this.accessKey = "";
    this.security = "";
  }

  public set(create: Account): void {
    this.username = create.username;
    this.accessKey = create.accessKey;
    this.security = create.security;
  }

  public toJSON(): Record<string, any> {
    return {
      username: this.username,
      accessKey: this.accessKey,
      security: this.security,
      version: this.version,
    };
  }

  public static fromJSON(json: string | Record<string, any>): Account {
    const data = typeof json === "string" ? JSON.parse(json) : json;
    const account = new Account(data.username || "", data.accessKey || "", data.security || "");
    if (typeof data.version === "number") {
      account.version = data.version;
    }
    return account;
  }

  public static create(username: string = "", accessKey: string = "", security: string = ""): Account {
    return new Account(username, accessKey, security);
  }
}

export default Account;
