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

liboqs-java/target/liboqs-java-<version>.jar


📁 Next Step
Copy the generated .jar file into your main project directory:

QuantumSecure/lib/


### 🔒 2. Java Cryptography Extension (JCE)
Purpose: Provides AES-256-GCM encryption used for symmetric data protection.
Note: JCE is already included in Java 8 and above — no additional installation required.

| Dependency              | Source                                                                | Used For                                                                    |
| ----------------------- | --------------------------------------------------------------------- | --------------------------------------------------------------------------- |
| **liboqs-java**         | [Open Quantum Safe](https://github.com/open-quantum-safe/liboqs-java) | CRYSTALS-Kyber (Key Encapsulation), CRYSTALS-Dilithium (Digital Signatures) |
| **JCE (Java built-in)** | Java SDK 8+                                                           | AES-256-GCM Encryption                                                      |


🧠 Important
After adding the .jar file to the /lib/ folder, compile and run the project using the following commands:

bash
Copy code
javac -cp "src;lib/*" -d bin src/*.java
java -cp "bin;lib/*" Main

💡 For Linux/Mac users:
Replace the semicolon ; with a colon : in the classpath (e.g., "src:lib/*").

This ensures the Java compiler and runtime can correctly access the liboqs-java library.
