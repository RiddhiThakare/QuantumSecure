# 🛡️ QuantumSecure  
Hybrid AES–Kyber–Dilithium framework for privacy-preserving healthcare file exchange.

---

## 📘 Overview  
QuantumSecure is a hybrid cryptographic framework that integrates classical and post-quantum algorithms to secure healthcare data exchange.  
It combines AES-256 for data encryption, CRYSTALS-Kyber for quantum-resistant key encapsulation, and CRYSTALS-Dilithium for digital signatures, ensuring confidentiality and integrity against next-generation threats.

---

## ⚙️ Features  
- 🔐 Hybrid AES + PQC encryption (AES-256 + Kyber)  
- ✍️ Post-quantum digital signatures (Dilithium)  
- 🧩 Modular Java architecture for encryption, signing, and verification  
- 📈 Performance-logging framework with real-time metrics  
- 💻 Works completely offline for secure local execution  
- 🏥 Designed and evaluated using healthcare data samples  
---

## 🧩 Tech Stack
- **Language:** Java SE (Object-Oriented, File I/O)
- **Encryption:** Java Cryptography Extension (AES-256, GCM mode)
- **Post-Quantum Layer:** liboqs-java (CRYSTALS-Kyber, CRYSTALS-Dilithium)
- **Frontend:** HTML, CSS, JavaScript
- **Build / Execution:** javac (manual compilation); liboqs-java built using Maven
- **Tools:** Git, VS Code, Linux Terminal
- **Domain Focus:** Healthcare Data Security
---

## ⚙️ Dependencies

Before running **QuantumSecure**, set up the required libraries as described below.

---

### 🧩 1. liboqs-java (for Kyber & Dilithium)

**Source:** [https://github.com/open-quantum-safe/liboqs-java](https://github.com/open-quantum-safe/liboqs-java)  
**Purpose:** Provides Java bindings for post-quantum algorithms **CRYSTALS-Kyber** and **CRYSTALS-Dilithium**.

#### 🧱 Steps to Build
```bash
# Clone the official repository
git clone https://github.com/open-quantum-safe/liboqs-java.git
cd liboqs-java

# Build the library using Maven
mvn clean package
```

📦 After Successful Build
The generated JAR file will be located at:
```
liboqs-java/target/liboqs-java-<version>.jar
```

📁 Next Step
Copy the generated .jar file into your main project directory:
```
QuantumSecure/lib/
```

### 🔒 2. Java Cryptography Extension (JCE)
Purpose: Provides AES-256-GCM encryption used for symmetric data protection.
Note: JCE is already included in Java 8 and above — no additional installation required.

| Dependency              | Source                                                                | Used For                                                                    |
| ----------------------- | --------------------------------------------------------------------- | --------------------------------------------------------------------------- |
| **liboqs-java**         | [Open Quantum Safe](https://github.com/open-quantum-safe/liboqs-java) | CRYSTALS-Kyber (Key Encapsulation), CRYSTALS-Dilithium (Digital Signatures) |
| **JCE (Java built-in)** | Java SDK 8+                                                           | AES-256-GCM Encryption                                                      |


🧠 Important
After adding the .jar file to the /lib/ folder, compile and run the project using the following commands:

```
javac -cp "src;lib/*" -d bin src/*.java
java -cp "bin;lib/*" Main
```

💡 For Linux/Mac users:
Replace the semicolon ; with a colon : in the classpath (e.g., "src:lib/*").

This ensures the Java compiler and runtime can correctly access the liboqs-java library.


---

## 🚀 How to Run

Once all dependencies are set up, follow the steps below to compile and execute the project.

---

### 🧩 1. Compile the Source Code

Use the following command to compile all `.java` files and place the compiled `.class` files inside the `bin/` directory:

```bash
javac -cp "src;lib/*" -d bin src/*.java
```

💡 Note:
The -cp flag specifies the classpath, including the lib folder.
The -d flag sets the destination directory for compiled .class files.
Ensure the lib folder contains the liboqs-java JAR before compiling.

🔐 2. Encrypt a File
To encrypt a file (for example, a CSV file named abc.csv in the input/ folder):
```
java -cp "bin;lib/*" Main enc input/abc.csv
```

After successful encryption:
The encrypted file will be saved in the /output/ folder.
The generated keys (Kyber/Dilithium) will be stored under /keys_generated/.
The console will display a confirmation message and hash of the ciphertext.

🔓 3. Decrypt a File
To decrypt a previously encrypted file:
```
java -cp "bin;lib/*" Main dec output/abc.csv.enc
```
After successful decryption:
The decrypted file will be placed in the /decrypted/ folder.
The system will automatically verify the integrity using the corresponding keys and signatures.

📁 Folder Structure
| Folder             | Description                                     |
| ------------------ | ----------------------------------------------- |
| `/src/`            | Contains Java source code files                 |
| `/bin/`            | Stores compiled `.class` files after build      |
| `/lib/`            | Holds dependency JARs (e.g., `liboqs-java.jar`) |
| `/input/`          | Input files for encryption                      |
| `/output/`         | Encrypted output files                          |
| `/decrypted/`      | Decrypted files after processing                |
| `/keys_generated/` | Generated Kyber and Dilithium key pairs         |
| `/web/`            | Frontend files (HTML, JS, CSS)                  |
| `.gitignore`       | Files and directories ignored by Git            |
| `README.md`        | Project documentation                           |


🧪 Example Run
```
# Compile
javac -cp "src;lib/*" -d bin src/*.java

# Encrypt
java -cp "bin;lib/*" Main enc input/patient_data.csv

# Decrypt
java -cp "bin;lib/*" Main dec output/patient_data.csv.enc
```

✅ Result:
The decrypted file will match the original input file, confirming successful encryption and signature validation.
