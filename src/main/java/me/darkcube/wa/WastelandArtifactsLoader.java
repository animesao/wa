package me.darkcube.wa;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

@SuppressWarnings("UnstableApiUsage")
public class WastelandArtifactsLoader implements PluginLoader {

    @Override
    public void classloader(PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();

        resolver.addRepository(new RemoteRepository.Builder(
                "central", "default", "https://repo1.maven.org/maven2"
        ).build());

        resolver.addRepository(new RemoteRepository.Builder(
                "papermc", "default", "https://repo.papermc.io/repository/maven-public/"
        ).build());

        resolver.addRepository(new RemoteRepository.Builder(
                "enginehub", "default", "https://maven.enginehub.org/repo/"
        ).build());

        resolver.addDependency(new Dependency(
                new DefaultArtifact("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2"), null
        ));
        resolver.addDependency(new Dependency(
                new DefaultArtifact("com.fasterxml.jackson.core:jackson-databind:2.17.2"), null
        ));

        classpathBuilder.addLibrary(resolver);
    }
}
