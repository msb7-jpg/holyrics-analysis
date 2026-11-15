import Utils from "../util/Utils.ts";

export interface I18NValue {
  val(): string;
  toString(): string;
}

export const I18N = {
  WORD_NAME: { val: () => "Nome", toString: () => "Nome" },
  WORD_SKILLS: { val: () => "Habilidades", toString: () => "Habilidades" },
  WORD_GROUPS: { val: () => "Grupos", toString: () => "Grupos" },
  WORD_VOICE: { val: () => "Voz", toString: () => "Voz" },
  WORD_INSTRUMENT: { val: () => "Instrumento", toString: () => "Instrumento" },
  WORD_ORCHESTRA: { val: () => "Orquestra", toString: () => "Orquestra" },
  WORD_OTHERS: { val: () => "Outros", toString: () => "Outros" },
};

export class EnumGroups {
  public static readonly VOICE = new EnumGroups("VOICE", 0, "Voz", "mic16.png");
  public static readonly INSTRUMENT = new EnumGroups("INSTRUMENT", 1, "Instrumento", "music16.png");
  public static readonly ORCHESTRA = new EnumGroups("ORCHESTRA", 2, "Orquestra", "violin16.png");
  public static readonly OTHERS = new EnumGroups("OTHERS", 3, "Outros", "others16.png");

  public readonly key: string;
  public readonly ordinal: number;
  public readonly name: string;
  public readonly image: string;

  private constructor(key: string, ordinal: number, defaultName: string, image: string) {
    this.key = key;
    this.ordinal = ordinal;
    this.name = defaultName;
    this.image = image;
  }

  public toString(): string {
    switch (this.key) {
      case "VOICE":
        return I18N.WORD_VOICE.val();
      case "INSTRUMENT":
        return I18N.WORD_INSTRUMENT.val();
      case "ORCHESTRA":
        return I18N.WORD_ORCHESTRA.val();
      case "OTHERS":
        return I18N.WORD_OTHERS.val();
      default:
        return this.name;
    }
  }

  public static values(): EnumGroups[] {
    return [EnumGroups.VOICE, EnumGroups.INSTRUMENT, EnumGroups.ORCHESTRA, EnumGroups.OTHERS];
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Member
 */
export class Member {
  public static readonly serialVersionUID = 12491;
  public static readonly EnumGroups = EnumGroups;

  public readonly id: number;
  private name: string;
  private readonly groups: Set<EnumGroups> = new Set<EnumGroups>();
  private skills: string;
  private active: boolean;
  private version: number = 1;

  constructor(
    name: string = "",
    skills: string = "",
    groups: EnumGroups[] = []
  ) {
    this.id = Utils.generateMillisecondId();
    this.name = name;
    this.skills = skills;
    for (const g of groups) {
      this.addGroup(g);
    }
    this.active = true;
  }

  public toString(): string {
    return this.name.length === 0 ? " " : this.name;
  }

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
  }

  public getSkills(): string {
    return this.skills;
  }

  public setSkills(skills: string): void {
    this.skills = skills;
  }

  public getGroups(): Set<EnumGroups> {
    return this.groups;
  }

  public getSortedGroups(): EnumGroups[] {
    return Array.from(this.groups).sort((a, b) => a.ordinal - b.ordinal);
  }

  public setGroup(group: EnumGroups, flag: boolean): void {
    if (flag) {
      this.addGroup(group);
    } else {
      this.removeGroup(group);
    }
  }

  public addGroup(group: EnumGroups): void {
    this.groups.add(group);
  }

  public removeGroup(group: EnumGroups): void {
    this.groups.delete(group);
  }

  public getMinGroup(): number {
    if (this.groups.size === 0) {
      return 999;
    }
    let min = 999;
    for (const g of this.groups) {
      if (g.ordinal < min) {
        min = g.ordinal;
      }
    }
    return min;
  }

  public isVoice(): boolean {
    return this.groups.has(EnumGroups.VOICE);
  }

  public isInstrument(): boolean {
    return this.groups.has(EnumGroups.INSTRUMENT);
  }

  public isOrchestra(): boolean {
    return this.groups.has(EnumGroups.ORCHESTRA);
  }

  public isOthers(): boolean {
    return this.groups.has(EnumGroups.OTHERS);
  }

  public isActive(): boolean {
    return this.active;
  }

  public setActive(active: boolean): void {
    this.active = active;
  }

  public groupsToString(): string {
    if (this.groups.size === 0) {
      return "";
    }
    const sorted = this.getSortedGroups();
    return sorted.map((g) => g.toString()).join(", ");
  }

  public toStringFull(): string {
    return `${I18N.WORD_NAME.val()}: ${this.name}\n${I18N.WORD_SKILLS.val()}: ${this.skills}\n${I18N.WORD_GROUPS.val()}: ${this.groupsToString()}`;
  }

  public toStringFullHTML(): string {
    return `<html>${I18N.WORD_NAME.val()}: ${this.name}<br>${I18N.WORD_SKILLS.val()}: ${this.skills}<br>${I18N.WORD_GROUPS.val()}: ${this.groupsToString()}`;
  }

  public equals(o: unknown): boolean {
    if (o instanceof Member) {
      return o.id === this.id;
    }
    return false;
  }

  public hashCode(): number {
    const big = BigInt(this.id);
    const high = Number(big >> 32n);
    const low = Number(big & 0xffffffffn);
    return 371 + ((high ^ low) | 0);
  }

  public static sortByGroups(): (o1: Member | null, o2: Member | null) => number {
    return (o1: Member | null, o2: Member | null) => {
      if (o1 == null) {
        return o2 == null ? 0 : -1;
      } else if (o2 == null) {
        return 1;
      } else if (o1.isActive() !== o2.isActive()) {
        return (o2.active ? 1 : 0) - (o1.active ? 1 : 0);
      } else {
        return o1.getMinGroup() !== o2.getMinGroup()
          ? o1.getMinGroup() - o2.getMinGroup()
          : o1.name.localeCompare(o2.name, undefined, { sensitivity: "accent" });
      }
    };
  }

  public getIcon(): { image: string } {
    const sorted = this.getSortedGroups();
    const first = sorted[0];
    const image = !first ? EnumGroups.OTHERS.image : first.image;
    return { image };
  }
}

export default Member;
