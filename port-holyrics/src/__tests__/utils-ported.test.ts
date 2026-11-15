import { describe, expect, test } from "bun:test";
import { StringUtils } from "../util/StringUtils";
import { Values } from "../util/Values";
import { MyProperties } from "../util/MyProperties";
import { Security } from "../util/Security";
import * as fs from "node:fs";

describe("StringUtils", () => {
  test("maxLength and maxLengthGetEnd", () => {
    expect(StringUtils.maxLength("abcdefgh", 5)).toBe("abcde");
    expect(StringUtils.maxLength("abcdefgh", 5, true)).toBe("abcde...");
    expect(StringUtils.maxLength("abc", 5, true)).toBe("abc");

    expect(StringUtils.maxLengthGetEnd("abcdefgh", 4)).toBe("efgh");
    expect(StringUtils.maxLengthGetEnd("abcdefgh", 4, true)).toBe("...efgh");
    expect(StringUtils.maxLengthGetEnd("abc", 4, true)).toBe("abc");
  });

  test("firstNumber", () => {
    expect(StringUtils.firstNumber("chapter 15 verse 2", 0)).toBe(15);
    expect(StringUtils.firstNumber("offset -42 value", 0)).toBe(-42);
    expect(StringUtils.firstNumber("no numbers here", 99)).toBe(99);
  });

  test("removeAccent and stringToSearch", () => {
    expect(StringUtils.removeAccent("Ação e Bênção")).toBe("Acao e Bencao");
    expect(StringUtils.stringToSearch("CoraÇÃO\npartido")).toBe("coracao partido");
  });

  test("removeExcessLineBreak", () => {
    expect(StringUtils.removeExcessLineBreak("\n\nhello\n\nworld\n\n")).toBe("hello\nworld");
    expect(StringUtils.removeExcessLineBreak(null)).toBeNull();
  });

  test("formatTimeHMS and formatTimeMm_Ss_MSms", () => {
    expect(StringUtils.formatTimeHMS(3661000)).toBe("01:01:01");
    expect(StringUtils.formatTimeMm_Ss_MSms(61123)).toBe("01m 01s 123ms");
  });

  test("wordWrap", () => {
    expect(StringUtils.wordWrap("1234567890", 3)).toBe("123 456 789 0");
    expect(StringUtils.wordWrap("abc", 5)).toBe("abc");
  });

  test("escapeHTML", () => {
    expect(StringUtils.escapeHTML('<div class="box">&\'hello\'\n</div>')).toBe(
      "&lt;div class=&#34;box&#34;&gt;&#38;&#39;hello&#39;<br>&lt;/div&gt;"
    );
  });

  test("onlyWordNumberAndSpace", () => {
    expect(StringUtils.onlyWordNumberAndSpace("Hello, World! 123 çãé #$")).toBe("Hello World 123 çãé ");
  });

  test("setupHighlight", () => {
    const text = "O Senhor é meu pastor, nada me faltará";
    const res = StringUtils.setupHighlight(text, "senhor");
    expect(res).toContain("<span style='background-color:#ffff00'>Senhor</span>");
  });
});

describe("Values", () => {
  test("constants are defined accurately", () => {
    expect(Values.APP_NAME).toBe("Holyrics");
    expect(Values.OBJ_KEY).toBe("#hly$k3y#");
    expect(Values.WHATSAPP).toBe("+55 15 98127 8370");
    expect(Values.VAGALUME_API_KEYS.length).toBe(20);
    expect(Values.EXTENSION_MUSIC).toBe(".muf");
    expect(Values.MAX_LENGTH_MUSIC_TITLE).toBe(80);
    expect(Values.BIBLE_MAP_DIVISION).toEqual([5, 12, 5, 17, 4, 1, 21, 1]);
    expect(Values.COLOR_BOOK_AND_CHAPTER.length).toBe(8);
  });
});

describe("MyProperties", () => {
  test("flags memory operations", () => {
    MyProperties.setFlag("flagKey", "flagVal");
    expect(MyProperties.getFlag("flagKey", "")).toBe("flagVal");
    expect(MyProperties.getFlag("missing", "default")).toBe("default");
  });

  test("get, set and mapStartsWith operations", () => {
    MyProperties.set("test.prefix.one", "1");
    MyProperties.set("test.prefix.two", "2");
    MyProperties.set("other.key", "3");

    expect(MyProperties.get("test.prefix.one")).toBe("1");
    expect(MyProperties.get("test.prefix.two")).toBe("2");
    expect(MyProperties.get("nonexistent", "fallback")).toBe("fallback");

    const mapped = MyProperties.mapStartsWith("test.prefix");
    expect(mapped["test.prefix.one"]).toBe("1");
    expect(mapped["test.prefix.two"]).toBe("2");
    expect(mapped["other.key"]).toBeUndefined();

    // Clean up test config.properties if created
    if (fs.existsSync("config.properties")) {
      fs.unlinkSync("config.properties");
    }
  });

  test("EnumProperties methods", () => {
    const enumItem = MyProperties.EnumProperties.COMMUNICATION_PANEL_CLOCK_SIZE;
    expect(enumItem.key).toBe("communication-panel-clock-size");
    expect(enumItem.getIntRange(50, 1000)).toBe(500);

    const availables = MyProperties.EnumProperties.availables();
    expect(availables.length).toBeGreaterThan(0);
    expect(availables.every((item) => item.available)).toBe(true);
  });
});

describe("Security", () => {
  const secretKey = "my-secret-key";

  test("getSHA256 and getPass16", () => {
    const sha = Security.getSHA256("test");
    expect(sha.length).toBe(64);
    expect(sha).toBe(sha.toUpperCase());

    const pass16 = Security.getPass16("test");
    expect(pass16.length).toBe(16);
    expect(pass16).toBe(sha.substring(0, 16));
  });

  test("AES CBC encrypt & decrypt", () => {
    const msg = "Mensagem confidencial Holyrics";
    const encrypted = Security.encrypt(msg, secretKey);
    const decrypted = Security.decrypt(encrypted, secretKey);
    expect(decrypted).toBe(msg);

    const rawBytes = Buffer.from([1, 2, 3, 4, 5, 6]);
    const encBytes = Security.encryptBytes(rawBytes, secretKey);
    const decBytes = Security.decryptBytes(encBytes, secretKey);
    expect(Buffer.from(decBytes)).toEqual(rawBytes);
  });

  test("AES ECB encryptCompatApp & decryptCompatApp", () => {
    const appMsg = "Holyrics App Communication";
    const enc = Security.encryptCompatApp(appMsg, secretKey);
    const dec = Security.decryptCompatApp(enc, secretKey);
    expect(dec).toBe(appMsg);
  });

  test("MD5 hashing with and without factor", () => {
    const md5Plain = Security.getMD5("holyrics");
    expect(md5Plain.length).toBeGreaterThan(0);

    const md5Factored = Security.getMD5("holyrics", 42);
    expect(md5Factored.length).toBeGreaterThan(0);
    expect(md5Factored).not.toBe(md5Plain);
  });

  test("Base64 helpers", () => {
    const original = "Holyrics 1:1 Port";
    const b64 = Security.toBase64(original);
    expect(Security.fromBase64(b64)).toBe(original);
  });
});
