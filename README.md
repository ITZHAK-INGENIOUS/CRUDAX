# CRUDAX

**C**reate · **R**ead · **U**pdate · **D**elete · **A**utomate · **X**A

> Un petit ordinateur minimaliste dans le téléphone.

CRUDAX est un **launcher Android minimaliste** qui organise applications, widgets, fichiers, Termux et automatisations derrière une interface extrêmement simple.

Ce n’est **pas** un nouveau système d’exploitation.  
C’est une **couche XA** au-dessus d’Android.

```
Android
  ↓
Termux / Shizuku / APIs Android
  ↓
CRUDAX Engine
  ↓
CRUDAX UI
  ↓
Utilisateur
```

---

## Vision

- Horloge + date
- Recherche globale
- Applications, Automatisations, Fichiers, Outils
- Widgets Android (AppWidgetHost)
- Intégration Termux (RUN_COMMAND)
- Shizuku optionnel
- Aucun crash si une intégration est absente

**Principe :** *Simple à utiliser. Puissant quand on veut aller plus loin.*

---

## Architecture

```
app/
├── core/           # Conteneur DI, constantes
├── ui/             # MainActivity, Apps, Automations, Tools, Onboarding
├── launcher/       # Rôle HOME
├── widgets/        # AppWidgetHost
├── apps/           # Package visibility, lancement
├── files/          # SAF, gestionnaires externes
├── cruda/          # Actions abstraites CREATE/READ/UPDATE/DELETE/AUTOMATE
├── automation/     # Scénarios, exécution, journal
├── termux/         # Détection + RUN_COMMAND
├── shizuku/        # Couche optionnelle
├── notifications/
├── permissions/
├── settings/
├── diagnostics/
└── storage/        # Room + DataStore
```

Toutes les intégrations passent par des **interfaces**.  
L’absence de Termux ou Shizuku ne fait jamais planter l’application.

---

## Niveaux de fonctionnement

| Niveau | Description |
|--------|-------------|
| 0 | Android seul |
| 1 | + Widgets |
| 2 | + Termux |
| 3 | + Termux:API |
| 4 | + Termux:GUI |
| 5 | + Shizuku |

Chaque niveau est utilisable indépendamment.

---

## Fonctionnalités × Intégrations

| Fonction | Sans Termux | Avec Termux | + API | + Shizuku |
|----------|:-----------:|:-----------:|:-----:|:---------:|
| Launcher HOME | ✓ | ✓ | ✓ | ✓ |
| Applications | ✓ | ✓ | ✓ | ✓ |
| Widgets | ✓ | ✓ | ✓ | ✓ |
| Fichiers (SAF) | ✓ | ✓ | ✓ | ✓ |
| Horloge / date | ✓ | ✓ | ✓ | ✓ |
| Automatisations basiques | ✓ | ✓ | ✓ | ✓ |
| Ouvrir Termux | — | ✓ | ✓ | ✓ |
| RUN_COMMAND | — | ✓* | ✓ | ✓ |
| Paquets Termux | — | ○ | ✓ | ✓ |
| Scripts XA | — | ○ | ✓ | ✓ |
| Infos système avancées | — | — | ○ | ✓ |

\* Nécessite `allow-external-apps=true` dans `~/.termux/termux.properties`.

---

## Compilation

### Prérequis

- JDK 17+
- Android SDK (API 34)
- Gradle 8.4+ (ou wrapper)

### Local

```bash
git clone <repo>
cd CRUDAX
chmod +x gradlew
./gradlew assembleDebug
```

APK : `app/build/outputs/apk/debug/app-debug.apk`

### GitHub Actions

Le workflow `.github/workflows/build-apk.yml` :

1. Checkout
2. Setup JDK 17
3. Setup Android SDK
4. Cache Gradle
5. `assembleDebug`
6. Publie l’artifact **CRUDAX-debug-apk**

---

## Installation

1. Installer l’APK (sources inconnues si besoin).
2. Au premier lancement : onboarding progressif (aucune rafale de permissions).
3. Optionnel : **Paramètres système → Applications par défaut → Écran d’accueil → CRUDAX**.

---

## Termux & RUN_COMMAND

1. Installer [Termux](https://github.com/termux/termux-app) (F-Droid recommandé).
2. Dans Termux :
   ```bash
   nano ~/.termux/termux.properties
   ```
   Ajouter / décommenter :
   ```
   allow-external-apps=true
   ```
3. Redémarrer Termux.
4. Dans CRUDAX → Outils → Connexion Termux : vérifier le diagnostic.

CRUDAX **n’installe jamais** Termux silencieusement et **ne simule pas** un accès shell s’il n’est pas autorisé.

---

## Widgets

CRUDAX utilise `AppWidgetHost` officiel.

- Mode édition (appui long sur l’écran) → **+** → Widget
- Le sélecteur système s’ouvre
- Position / taille persistées localement
- Suppression libère l’`AppWidgetId`

---

## Automatisations

Page **Automatisations** → **+**

Types d’actions (moteur CRUDA) :

- Fichier / Dossier
- Commande Termux / Script
- Application / Intent
- Notification / Attente
- …

Actions classées **LOW / MEDIUM / HIGH**.  
Les actions HIGH demandent une confirmation explicite.

Journal visible (stdout / stderr / code de sortie lorsque disponible).

---

## Permissions

| Permission | Pourquoi | Quand |
|------------|----------|-------|
| `INTERNET` | Docs, liens | Manifest |
| `POST_NOTIFICATIONS` | Fin d’auto, erreurs | Runtime, à la demande |
| `SYSTEM_ALERT_WINDOW` | Contrôles flottants | Runtime, optionnel |
| `RECEIVE_BOOT_COMPLETED` | Auto au boot | Désactivé par défaut |

**Jamais** `MANAGE_EXTERNAL_STORAGE` par commodité.  
SAF + URI persistants prioritaires.

Page **Diagnostic** et **Autorisations** affichent l’état réel.

---

## Sécurité

- Pas de contournement des protections Android
- Pas d’API non documentée quand une API officielle existe
- Intégrations isolées derrière interfaces
- Confirmations pour les actions dangereuses
- Aucune donnée utilisateur envoyée sans consentement

---

## Dépannage

| Problème | Solution |
|----------|----------|
| CRUDAX n’apparaît pas comme launcher | Vérifier `CATEGORY_HOME` + redémarrer |
| Termux « non autorisé » | `allow-external-apps=true` + redémarrer Termux |
| Widget ne s’ajoute pas | Consentement bind requis par Android |
| Crash au démarrage | Ouvrir Diagnostic ; vérifier absence de dépendance native manquante |
| Permissions refusées | CRUDAX continue en mode dégradé |

---

## Roadmap

- **v1** (ce dépôt) : launcher, apps, widgets host, Termux détection + RUN_COMMAND, automatisations basiques, diagnostic, CI
- **v2** : éditeur d’automatisations riche, Room complet, paquets Termux, sessions
- **v3** : Termux:GUI, Shizuku binder, triggers (boot / wifi / time), command center avancé

---

## Licence

MIT — voir [LICENSE](LICENSE).

---

**CRUDAX** — *Simple. Puissant. Local.*
