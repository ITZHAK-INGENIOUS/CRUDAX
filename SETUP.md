# Setup rapide

Si `gradlew` échoue (wrapper jar manquant) :

```bash
# Avec Gradle installé globalement
gradle wrapper --gradle-version 8.4

# Puis
./gradlew assembleDebug
```

Ou ouvrez le projet dans Android Studio (File → Open → dossier CRUDAX).
Android Studio régénère le wrapper automatiquement.
