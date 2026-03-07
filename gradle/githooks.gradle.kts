tasks.register<Copy>("installGitHooks") {
    from("$rootDir/quality/git-hooks/")
    into("$rootDir/.git/hooks/")

    filePermissions {
        unix("rwxr-xr-x")
    }
}