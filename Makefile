# Dossiers
SRC_DIR := Benfdal_Croizier/src
OUT_DIR := Benfdal_Croizier/out

# Sources Java
SRCS := $(shell find $(SRC_DIR) -name "*.java")

# Java ZuluFX (adapter JAVA_HOME sous Windows ou commenter sous Linux)
JAVA_HOME := C:/Users/skeel/Documents/cours/Gestion\ de\ projet/zulu21.40.17-ca-fx-jdk21.0.6-win_x64
JC := $(JAVA_HOME)/bin/javac
JAVA := $(JAVA_HOME)/bin/java

# Flags
JCFLAGS := -d $(OUT_DIR) -cp $(SRC_DIR)
JAVAFLAGS := -cp $(OUT_DIR)

# Classe principale
MAIN_CLASS := Main

.PHONY: all run clean

all: clean
	@echo 🔧 Compilation Java...
	@$(JC) $(JCFLAGS) $(SRCS)
	@echo ✅ Compilation terminée

run: all
	@echo 🚀 Lancement...
	@$(JAVA) $(JAVAFLAGS) $(MAIN_CLASS)

clean:
	@echo 🧹 Nettoyage...
	@rm -rf $(OUT_DIR)/*
	@echo ✅ Nettoyage terminé