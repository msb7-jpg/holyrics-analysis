import { ControllerFactory } from "./ControllerFactory.ts";
import { Member, EnumGroups } from "../model/Member.ts";
import { MemberController as ScheduleMemberStub } from "../model/Schedule.ts";

(ScheduleMemberStub as any).get = (id: number) => {
  return MemberController.get(id);
};

/**
 * Revives raw JSON objects into Member instances.
 */
function reviveMember(data: any): Member {
  if (data instanceof Member) {
    return data;
  }
  const groupsList: EnumGroups[] = [];
  if (Array.isArray(data.groups)) {
    for (const g of data.groups) {
      const key = typeof g === "string" ? g : g?.key;
      const found = EnumGroups.values().find((item) => item.key === key);
      if (found) {
        groupsList.push(found);
      }
    }
  }
  const member = new Member(data.name ?? "", data.skills ?? "", groupsList);
  if (data.id !== undefined) {
    (member as any).id = Number(data.id);
  }
  if (data.active !== undefined) {
    member.setActive(Boolean(data.active));
  }
  if (data.version !== undefined) {
    (member as any).version = Number(data.version);
  }
  return member;
}

/**
 * Controller for Member (Staff) management.
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.MemberController
 */
export class MemberController {
  public static readonly serialVersionUID: bigint = 12491n;
  private static readonly FILE_NAME: string = "staff.hly";
  private static readonly STAFF: Member[] = ControllerFactory.load<Member>(
    MemberController.FILE_NAME,
    reviveMember
  );

  public static add(m: Member): void {
    const exists = MemberController.STAFF.some((item) =>
      item.equals ? item.equals(m) : item.id === m.id
    );
    if (!exists) {
      MemberController.STAFF.push(m);
      MemberController.save();
    }
  }

  public static remove(m: Member): void {
    const index = MemberController.STAFF.findIndex((item) =>
      item.equals ? item.equals(m) : item.id === m.id
    );
    if (index !== -1) {
      MemberController.STAFF.splice(index, 1);
      MemberController.save();
    }
  }

  public static getAll(onlyActive: boolean = false): Member[] {
    return MemberController.STAFF.filter((m) => !onlyActive || m.isActive());
  }

  public static get(id: number): Member | null {
    return MemberController.STAFF.find((m) => m.id === id) ?? null;
  }

  public static save(): void {
    MemberController.sort();
    ControllerFactory.save(MemberController.STAFF, MemberController.FILE_NAME);
  }

  public static sort(): void {
    MemberController.STAFF.sort(Member.sortByGroups());
  }

  public static saveList(list: Member[]): void {
    MemberController.STAFF.length = 0;
    const seen = new Set<number>();
    const distinct: Member[] = [];
    for (const item of list) {
      if (!seen.has(item.id)) {
        seen.add(item.id);
        distinct.push(item);
      }
    }
    MemberController.STAFF.push(...distinct);
    MemberController.save();
  }
}

export default MemberController;
