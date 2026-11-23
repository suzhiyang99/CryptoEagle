#!/usr/bin/env python3
"""Writes audit_notes.json: the manual verdicts and reasons recorded after reading the source of every alert that the
benchmark does not confirm, and of every missed or wrongly flagged benchmark case. build_tables.py merges them in.

Alert key: alert|<dataset>|<file key>|<line>|<rule>   Case key: case|<dataset>|<case id>|<rule>
"""
import json
from pathlib import Path

CORRECT = "Correct: real misuse not labelled by the benchmark"
THRESHOLD = "Threshold-dependent"
INCORRECT = "Incorrect"
LABEL_ERR = "Disagrees with label: threshold"

ITER_THRESHOLD = ("The tool requires at least 10,000 PBKDF/PBE iterations (crypto-ext CryptoOntology."
                  "MinimumPbkdf2Iterations); the benchmark only treats counts below 1,000 as misuse.")

notes = {}


def alert(dataset, key, line, rule, verdict, reason):
    notes[f"alert|{dataset}|{key}|{line}|{rule}"] = {"verdict": verdict, "reason": reason}


def case(dataset, case_id, rule, reason, fn_category=""):
    notes[f"case|{dataset}|{case_id}|{rule}"] = {"reason": reason, **({"fn_category": fn_category} if fn_category else {})}


# FN categories used in the paper's RQ1 discussion of the CryptoAPI-Bench misses.
NOT_REACHED = "misuse not reached"


# ---------------- CryptoAPI-Bench: alerts outside the labelled universe ----------------
J = "cryptoapi-bench"
for key, line in (("brokencryptoabicase1", 14), ("brokencryptoabicase11", 18), ("brokencryptoabicase5", 22),
                  ("brokencryptoabmc1", 14), ("brokencryptoabscase1", 30), ("brokencryptobbcase1", 15)):
    alert(J, key, line, "C1", CORRECT,
          'The transformation reaching Cipher.getInstance is the literal "DES/ECB/PKCS5Padding", so ECB is used. '
          "The benchmark labels these files only as 'DES used' (C14), which the tool also reports.")
for key, line, algo in (("brokencryptoabscase1", 34, "DES/ECB"), ("brokencryptoabscase2", 34, "Blowfish"),
                        ("brokencryptoabscase3", 34, "RC4"), ("brokencryptoabscase4", 35, "RC2"),
                        ("brokencryptoabscase5", 35, "IDEA")):
    alert(J, key, line, "C4", CORRECT,
          f"{algo} encrypts data through cipher.doFinal and the file applies no MAC or AEAD mode, which is C4. "
          "The benchmark has no authentication category; these files are labelled only for the weak cipher.")
for key, line in (("credentialinstringabscase1", 57), ("credentialinstringbbcase1", 24), ("credentialinstringcorrected", 23),
                  ("predictablecryptographickeyabscase1", 57), ("predictablecryptographickeycorrected", 23)):
    alert(J, key, line, "C4", CORRECT,
          "AES/CBC/PKCS5Padding encrypts data through cipher.doFinal with no MAC, which is C4. 'Corrected' files are "
          "corrected only for the key/credential problem; the benchmark does not label authentication.")
for key, line in (("staticsaltsabhcase1", 25), ("staticsaltsabicase1", 16), ("staticsaltsabicase2", 27),
                  ("staticsaltsabicase3", 18), ("staticsaltsabmc1", 8), ("staticsaltsabpscase1", 19),
                  ("staticsaltsabscase1", 27), ("staticsaltsbbcase1", 15), ("staticsaltscorrected", 17)):
    alert(J, key, line, "C7", THRESHOLD,
          f"The PBEParameterSpec uses count = 1020. {ITER_THRESHOLD} These files are labelled only for the salt.")
alert(J, "lessthan1000iterationpbeabpscase1", 19, "C7", LABEL_ERR,
      f"count is 5, then 1050 on the taken path; both values are below 10,000. {ITER_THRESHOLD}")
alert(J, "lessthan1000iterationpbecorrected", 17, "C7", LABEL_ERR, f"count = 1020. {ITER_THRESHOLD}")

# ---------------- CryptoAPI-Bench: missed and wrongly flagged cases ----------------
case(J, "xlsx-row-136", "C3",
     "Byte.parseByte receives the non-numeric text \"abcde\" and throws NumberFormatException before the IV is constructed.",
     NOT_REACHED)
for row, rule, material in ((134, "C8", "salt"), (133, "C9", "seed")):
    case(J, f"xlsx-row-{row}", rule,
         f"Byte.parseByte receives a char[] identity string from {material}.toString() and throws "
         f"NumberFormatException before the {material} reaches the cryptographic API.", NOT_REACHED)
case(J, "xlsx-row-93", "C2",
     "The fixed key is selected only when method1 receives a null byte array. The benchmark caller passes the "
     "result of String.getBytes(), which is non-null whenever the call succeeds. The fallback is unreachable "
     "on this call path.", NOT_REACHED)
case(J, "xlsx-row-176", "C9",
     "The scorer resolves row 176's ABPM filenames to PredictableSeedsABMCCase2.java and PredictableSeedsABMC2.java. "
     "C9 flags the fixed seed at PredictableSeedsABMC2.java:7.")
alert(J, "predictableseedsabmc2", 7, "C9", "Agrees with label",
      "PredictableSeedsABMCCase2 passes the fixed byte 100 to the SecureRandom constructor in PredictableSeedsABMC2. "
      "The two benchmark filename aliases associate this alert with row 176.")
alert(J, "brokenmacbbcase2", 16, "C15", "Agrees with label",
      "The CryptoAPI-Bench run enables the legacy HMAC-SHA1 policy. C15 reports the HmacSHA1 MAC call in row 32.")
case(J, "xlsx-row-158", "C7", f"count is 5, then 1050 on the taken path. {ITER_THRESHOLD}")
case(J, "xlsx-row-38", "C7", f"count = 1020. {ITER_THRESHOLD}")

for key, lines in (("insecureasymmetriccipherabicase1", (16, 24)), ("insecureasymmetriccipherabicase2", (16, 24)),
                   ("insecureasymmetriccipherabicase3", (23, 31)), ("insecureasymmetriccipherabmc1", (16, 17)),
                   ("insecureasymmetriccipherbbcase1", (15, 23))):
    for line in lines:
        alert(J, key, line, "C11", CORRECT,
              'Cipher.getInstance("RSA") resolves to RSA/ECB/PKCS1Padding in the JDK providers and is used to encrypt '
              "with the public key (or decrypt with the private key): RSA PKCS#1 v1.5 encryption. The benchmark labels "
              "these files only for the 1024-bit key (C13), which the tool also reports.")
alert(J, "insecureasymmetriccipherabpscase1", 20, "C11", CORRECT,
      'Cipher.getInstance("RSA") resolves to RSA/ECB/PKCS1Padding and encrypts with the public key: RSA PKCS#1 v1.5 '
      "encryption. The benchmark labels this file as not a misuse because its key size is path-sensitive (1,024 or "
      "2,048 bits by `choice`); the padding is not labelled. At file level this is the one new C1–C18 FP.")

case(J, "xlsx-row-131", "C18",
     "The static field chain supplies a char[] to url.toString(). The resulting array identity string "
     "has no HTTP scheme, and the URL constructor throws MalformedURLException.", NOT_REACHED)

alert(J, "credentialinstringabicase2", 27, "C2", CORRECT,
      "The AES key reaches SecretKeySpec through the static-field chain go2 -> go3 -> go from DEFAULT_ENCRYPT_KEY = "
      "{20, 10, 30, ...}: a hard-coded key. The benchmark labels this file only as 'Credential in String'.")

# ---------------- PyCryptoBench ----------------
P = "pycryptobench"
for name, line in (("rule_07_field-sensitive_0", 11), ("rule_07_field-sensitive_1", 11), ("rule_07_global_0", 8),
                   ("rule_07_global_1", 8), ("rule_07_insecure_0", 3), ("rule_07_insecure_1", 4),
                   ("rule_07_interprocedural_0", 5), ("rule_07_interprocedural_1", 5),
                   ("rule_07_interproceduralviareturn_0", 7), ("rule_07_interproceduralviareturn_1", 7),
                   ("rule_07_path-sensitive_0", 4), ("rule_07_path-sensitive_1", 4)):
    alert(P, name, line, "C2", CORRECT,
          "The AES key is the literal b'1234123412341234'. Benchmark Rule 7 labels only the ECB mode (C1).")
for name, line in (("rule_09_field-sensitive_0", 18), ("rule_09_field-sensitive_1", 14), ("rule_09_global_0", 11),
                   ("rule_09_global_1", 11), ("rule_09_insecure_0", 6), ("rule_09_interprocedural_0", 8),
                   ("rule_09_interprocedural_1", 8), ("rule_09_interproceduralviareturn_0", 10),
                   ("rule_09_interproceduralviareturn_1", 10), ("rule_09_path-sensitive_0", 7),
                   ("rule_09_path-sensitive_1", 7)):
    alert(P, name, line, "C2", CORRECT,
          "The AES key is the literal b'Sixteen byte key'. Benchmark Rule 9 labels only unauthenticated encryption (C4).")
alert(P, "rule_09_insecure_1", 5, "C2", CORRECT, "The DES key is the literal b'12345678'.")
alert(P, "rule_09_insecure_1", 5, "C14", CORRECT, "DES is a deprecated cipher (56-bit key).")
import re as _re
for _f in sorted(Path(__file__).resolve().parent.joinpath("raw", "pycryptobench").glob("findings.json")):
    for _x in json.loads(_f.read_text()):
        _n = Path(_x["srcFile"]).stem.lower()
        if _x["ruleId"] == "C2" and _re.match(r"rule_0[68]_", _n):
            alert(P, _n, _x["lineNo"], "C2", CORRECT,
                  "pbkdf2_hmac derives the hash from the literal password b'SomePassword...'/b'someverylongpassword': "
                  "a hard-coded KDF password. Benchmark Rules 6 and 8 label only the salt or iteration count.")
case(P, "jsonl-1688", "C8",
     "The salt literal is passed from starting_method() as an argument to call_method(); this interprocedural "
     "parameter flow into pbkdf2_hmac's salt is not resolved, so the salt is not proven constant.")
case(P, "jsonl-1663", "C18",
     "urllib.request.urlopen is passed as a function value and called as argument('http://google.com'). The network-sink "
     "check matches calls to urlopen/requests by name or receiver; a first-class function parameter is not resolved.")
case(P, "jsonl-1668", "C18",
     "urllib.request.Request('http://google.com') only builds a request object that is printed and never opened. By "
     "design C18 requires a URL that reaches an API that opens a connection, so no alert is raised.")
case(P, "jsonl-1755", "C17",
     "The options dict {\"verify_signature\": False} is a module global read through `global x` inside the function; "
     "the JWT check reads the dict literal at the decode call and does not follow the global.")
case(P, "jsonl-1759", "C17",
     "The jwt module is passed as a parameter (call_method(jwt)) and used as argument.decode(...). The receiver is not "
     "resolved to the jwt module, so the call is not recognised as jwt.decode.")
_tls = ("ssl.wrap_socket(ssl_version=...) receives ssl.PROTOCOL_SSLv2 {0}; the deprecated-protocol check reads the "
        "constant at the call or in a local/module assignment and does not resolve this flow.")
case(P, "jsonl-1765", "C18", _tls.format("through an object field set by BaseRunner.__init__ (runner_object.argument)"))
case(P, "jsonl-1768", "C18", _tls.format("through a module global read with `global x` inside the function"))
case(P, "jsonl-1769", "C18", _tls.format("as an argument to a nested function returned by call_method()"))
case(P, "jsonl-1771", "C18", _tls.format("as an argument passed from starting_method() into call_method(argument)"))
case(P, "jsonl-1721", "C4",
     "AES.new(key, AES.MODE_CFB, iv) is created but never used to encrypt. By design C4 requires encryption-use "
     "evidence (an encrypt call), so an unused cipher object is not reported. The tool still reports the literal key (C2).")
case(P, "jsonl-1728", "C4",
     "DES.new(key, DES.MODE_OFB, ...) is created but never used to encrypt, so C4 is not raised (same design choice). "
     "The tool reports the weak cipher (C14) and the literal key (C2) on the same line.")

# ---------------- ApacheCryptoAPI-Bench ----------------
A = "apache"
ART = "artemis-commons-2.5.0-SNAPSHOT/org/apache/activemq/artemis/utils/DefaultSensitiveStringCodec.java"
ART_RANDOM = "artemis-commons-2.5.0-SNAPSHOT/org/apache/activemq/artemis/utils/RandomUtil.java"
TAVERNA = ("taverna-perspective-myexperiment-3.1.0-incubating-SNAPSHOT/org/apache/taverna/ui/perspectives/"
           "myexperiment/model/MyExperimentClient.java")
alert(A, "apacheds-kerberos-codec-2.0.0-M25-SNAPSHOT/org/apache/directory/server/kerberos/shared/crypto/encryption/DesStringToKey.java",
      135, "C4", INCORRECT,
      "calculateChecksum() runs DES-CBC only to compute the Kerberos des-string-to-key checksum (it keeps the last "
      "block, a CBC-MAC). It does not encrypt data for confidentiality, so 'encryption without authentication' does not apply.")
for line in (109, 131, 138, 139):
    alert(A, ART, line, "C12", CORRECT,
          'The Blowfish key is "clusterpassword".getBytes(): a password string used directly as key bytes without a KDF. '
          "Two operations (encode/decode), each reported at the key-spec and the Cipher line.")
alert(A, ART, 141, "C4", CORRECT, "Blowfish encrypts through doFinal and the result is only hex-encoded; no MAC is applied.")
alert(A, ART, 175, "C7", THRESHOLD, f"The iterations field defaults to 1024 (configurable through params). {ITER_THRESHOLD}")
for rule in ("C8", "C9"):
    alert(A, ART, 175, rule, CORRECT,
          "PBKDF2Algorithm.getSalt() calls RandomUtil.randomBytes(int), which fills the salt through randomByte() "
          "using java.util.Random.nextInt(). The resulting salt reaches PBEKeySpec. The workbook labels the random "
          "source in RandomUtil.java, so the alert at this KDF call is outside the scored site.")
alert(A, ART_RANDOM, 79, "C9", "Agrees with label",
      "The reported nextInt() call in randomByte() supplies bytes to the PBKDF2 salt. It matches workbook row 10 "
      "at this decompiled source line.")
alert(A, TAVERNA, 219, "C18", "Agrees with label",
      "The constructor uses http://www.myexperiment.org when the configured base URL is missing or empty. "
      "A URL built from that fallback reaches openConnection() in doMyExperimentGET.")
alert(A, ART, 187, "C7", INCORRECT,
      "verify() parses the iteration count from the stored hash string (Integer.parseInt(parts[0])); there is no "
      "constant evidence for this operation, so the alert is not supported.")
alert(A, "deltaspike-core-impl-1.8.1-SNAPSHOT/org/apache/deltaspike/core/impl/crypto/DefaultCipherService.java", 97, "C4", CORRECT,
      'Cipher.getInstance("AES") defaults to AES/ECB/PKCS5Padding; data is encrypted through doFinal with no MAC.')
alert(A, "mcf-core-2.9-SNAPSHOT/org/apache/manifoldcf/core/system/ManifoldCF.java", 431, "C7", LABEL_ERR,
      f"PBKDF2WithHmacSHA1 with 1024 iterations. {ITER_THRESHOLD}")
alert(A, "mcf-core-2.9-SNAPSHOT/org/apache/manifoldcf/core/system/ManifoldCF.java", 470, "C4", CORRECT,
      "encrypt() gets an AES/CBC cipher from getCipher(salt, 1, passCode, iv) (mode 1 = ENCRYPT_MODE) and encrypts "
      "through a CipherOutputStream; the output is only Base64-encoded and no MAC is applied anywhere in the class.")
SQL = "openejb-core-7.0.5-SNAPSHOT/org/apache/openejb/core/security/jaas/SQLLoginModule.java"
alert(A, SQL, 76, "C5", INCORRECT,
      "The digest algorithm comes from the login-module options at run time (this.digest); the call only checks that it "
      "exists. The reported algorithm '3DES' is a value-resolution error.")
alert(A, SQL, 299, "C6", CORRECT,
      "checkPassword() hashes the provided password with a single MessageDigest call (no salt, no KDF) and compares it "
      "with the stored value: a password hashed without a KDF. This class is not part of the benchmark.")
case(A, "xlsx-row-112", "C7", f"1024 iterations. {ITER_THRESHOLD}")

Path(__file__).with_name("audit_notes.json").write_text(json.dumps(notes, indent=1, ensure_ascii=False) + "\n", encoding="utf-8")
print(len(notes), "notes")
