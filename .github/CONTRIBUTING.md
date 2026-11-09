# How to contribute

Changes are welcome. A few guidelines keep things manageable.

Restricted Cells only works with [AE2 UELM](https://github.com/AE2-Unofficial-Extended-Life-Modern/AE2-UELM), so test against UELM and not the original AE2.

## Getting Started

### Reporting an Issue

Found a bug, a crash or have a suggestion? Create an issue.

1. Make sure it has not been reported already and that you use the latest version.
2. Go to the Issues tab and click `New issue`.
3. Use one of the templates and fill in what it asks for.

Include the Minecraft, Forge and UELM versions, the versions of MEGA Cells, Expanded AE and Applied Mekanistics if you have them, and the full log or crash report from a paste site. Issues without these can be closed.

### Building

You need Java 17.

```
./gradlew build
./gradlew runClient
./gradlew runData
```

`runData` writes the lang files and recipes in `src/generated`. Commit them with your change. The dev client loads MEGA Cells, Expanded AE and Applied Mekanistics. Set `runtime_cell_addons` in `gradle.properties` to load fewer. `runData` only writes what is loaded, so run it with all of them before you commit.

### Submitting Changes

* Open an issue first, unless one exists or the change is small.
* Fork the repository and create a topic branch from `main`. Please avoid working directly on `main`.
* Make commits of logical units.
* Check for unnecessary whitespace with `git diff --check` before committing.
* Make sure your commit messages are in the proper format.

````
    (#12) Add the Applied Mekanistics chemical cells

    Without this patch the mod has no chemical cells, so players with
    Mekanism cannot cap them. This patch adds the 1k to 256k cells and the
    MEGA versions, with recipes and guide pages.

    The first line is an imperative statement with the issue number. The
    body describes the behavior without the patch, why this is a problem,
    and how the patch fixes it.
````

* Test your changes in the dev client, and describe how you tested them and which add-ons were loaded.
* Add an entry to `CHANGELOG.md`.

By contributing, you agree to license your code under LGPL-3.0-or-later and your textures under CC BY-NC-SA 3.0.

## Making Trivial Changes

### Documentation

For trivial changes to documentation, guide pages and translations, it is not always necessary to create a new issue. We usually use `squash and merge`. This allows us to easily append the PR number for future reference.

````
    (doc) Fix the equal distribution guide page

    The German page said the amount applies to the whole cell. It applies
    to each type when the card is installed.
````

### Semantic Changes

PRs that only reformat or rename code will be rejected. Code clean-up should be coordinated with the maintainer first.

# Additional Resources

* [General GitHub documentation](https://docs.github.com/)
* [GitHub pull request documentation](https://docs.github.com/pull-requests)
