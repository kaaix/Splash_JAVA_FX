# -------------------------------------------------------------------
# CONFIGURATION (modifiez seulement USER_JAVA_HOME si besoin)
# -------------------------------------------------------------------
USER_JAVA_HOME ?= C:/Users/skeel/Documents/cours/Gestion\ de\ projet/zulu21.40.17-ca-fx-jdk21.0.6-win_x64

# Répertoires
SRC_DIR    := Benfdal_Croizier/src
OUT_DIR    := Benfdal_Croizier/out

# Classe principale
MAIN_CLASS := Main

# Choix du compilateur et de java
ifeq ($(USER_JAVA_HOME),)
  JAVAC := javac
  JAVA  := java
else
  JAVAC := $(USER_JAVA_HOME)/bin/javac
  JAVA  := $(USER_JAVA_HOME)/bin/java
endif

# Séparateur de classpath (Windows: ';', Linux/mac: ':')
UNAME_S := $(shell uname -s)
ifeq ($(OS),Windows_NT)
  SEP := ;
else ifneq (,$(findstring MINGW,$(UNAME_S)))
  SEP := ;
else
  SEP := :
endif

# Flags
JCFLAGS   := -d $(OUT_DIR) -cp "$(SRC_DIR)"
JAVAFLAGS := -cp "$(OUT_DIR)$(SEP)$(SRC_DIR)"

# Commandes portables
MKDIR := mkdir -p
RM    := rm -rf
CP    := cp -r

.PHONY: all run clean

# 1) Prépare le dossier out
all: clean $(OUT_DIR)
	@echo 🔧 Compilation Java…
	@$(JAVAC) $(JCFLAGS) $(shell find $(SRC_DIR) -name "*.java")
	@echo 🔧 Copie des ressources…
	@$(CP) $(SRC_DIR)/assets  $(OUT_DIR) || true
	@$(CP) $(SRC_DIR)/config  $(OUT_DIR) || true
	@$(CP) $(SRC_DIR)/lang    $(OUT_DIR) || true
	@echo ✅ Projet prêt

# create out/
$(OUT_DIR):
	@$(MKDIR) $(OUT_DIR)

# Vide out/ avant chaque build
clean:
	@echo 🧹 Nettoyage…
	@$(RM) $(OUT_DIR)
	@echo ✅ Nettoyage terminé

# 2) Compile puis lance votre application
run: all
	@echo 🚀 Lancement de $(MAIN_CLASS)…
	@$(JAVA) $(JAVAFLAGS) $(MAIN_CLASS)
