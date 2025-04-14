# Dossiers 
SRC_DIR := src
OUT_DIR := out

# Détection automatique des fichiers source
SRCS := $(shell find $(SRC_DIR) -name "*.java")

# Chemin vers Zulu JavaFX avec JavaFX intégré
JC := /home/mahdi/S6/JAVAFX/zulu21/bin/javac
JAVA := /home/mahdi/S6/JAVAFX/zulu21/bin/java

# Compilation sans --module-path (Zulu JDK intègre déjà JavaFX)
JCFLAGS := -d $(OUT_DIR) -cp $(SRC_DIR)
JAVAFLAGS := -cp $(OUT_DIR)

# Règles
.PHONY: all run run-console clean

all:
	@echo '🔧 Compilation JavaFX...'
	@$(JC) $(JCFLAGS) $(SRCS)

run:
	@echo '🚀 Lancement interface graphique...'
	@$(JAVA) $(JAVAFLAGS) Vues.Menu.FlashCrashMenu

run-console:
	@echo '🖥️  Lancement version console...'
	@$(JAVA) $(JAVAFLAGS) Modeles.Program
clean:
	@echo '🗑️  Nettoyage...'
	@rm -rf $(OUT_DIR)/*
	@find . -type f -name "*:Zone.Identifier" -exec rm -f {} +
	@echo "✅ Fichiers supprimés"
