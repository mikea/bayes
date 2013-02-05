package com.mikea.bayes.dbg;

import dagger.Module;
import dagger.Provides;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateModelException;
import org.eclipse.jetty.server.Server;

import javax.inject.Singleton;

/**
 * @author mike.aizatsky@gmail.com
 */
@Module(
        entryPoints = {
                DbgServer.class,
                DbgServer.RootHandler.class,
        }
)
public class DbgModule {
    @Provides
    @Singleton
    Server provideServer() {
        return new Server(8080);
    }

    @Provides
    @Singleton
    public Configuration provideFreemarkerConfiguration() {
        Configuration cfg = new Configuration();
        cfg.setClassForTemplateLoading(DbgModule.class, "/" + DbgModule.class.getPackage().getName().replace('.', '/') + "/");

        try {
            cfg.setSharedVariable("ver", Math.round(Math.random()*1000));
        } catch (TemplateModelException e) {
            throw new RuntimeException(e);
        }

        DefaultObjectWrapper wrapper = new DefaultObjectWrapper();
        wrapper.setExposeFields(true);
        cfg.setObjectWrapper(wrapper);
        return cfg;
    }

}
