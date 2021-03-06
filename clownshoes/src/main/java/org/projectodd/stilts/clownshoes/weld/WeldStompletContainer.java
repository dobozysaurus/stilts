/*
 * Copyright 2011 Red Hat, Inc, and individual contributors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.projectodd.stilts.clownshoes.weld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.jboss.weld.bootstrap.WeldBootstrap;
import org.jboss.weld.bootstrap.api.Environments;
import org.jboss.weld.manager.BeanManagerImpl;
import org.projectodd.stilts.StompException;
import org.projectodd.stilts.clownshoes.stomplet.NoSuchStompletException;
import org.projectodd.stilts.clownshoes.stomplet.SimpleStompletContainer;
import org.projectodd.stilts.stomplet.Stomplet;

public class WeldStompletContainer extends SimpleStompletContainer {

    public WeldStompletContainer(boolean includeCore) {
        this.includeCore = includeCore;
    }

    public void addStomplet(String pattern, String className) throws StompException {
        addStomplet( pattern, className, new HashMap<String,String>() );
    }
    
    public void addStomplet(String pattern, String className, Map<String,String> properties) throws StompException {
        try {
            Stomplet stomplet = newStomplet( className );
            addStomplet( pattern, stomplet, properties );
        } catch (ClassNotFoundException e) {
            throw new StompException( e );
        }
    }

    protected Stomplet newStomplet(String className) throws ClassNotFoundException, NoSuchStompletException {

        Class<?> stompletImplClass = this.deployment.getClassLoader().loadClass( className );
        Set<Bean<?>> beans = this.beanManager.getBeans( stompletImplClass );

        if (beans.isEmpty()) {
            throw new NoSuchStompletException( className );
        }

        Bean<? extends Object> bean = this.beanManager.resolve( beans );

        CreationalContext<?> creationalContext = this.beanManager.createCreationalContext( bean );
        Stomplet stomplet = (Stomplet) beanManager.getReference( bean, stompletImplClass, creationalContext );

        return stomplet;
    }
    
    public void addBeanDeploymentArchive(CircusBeanDeploymentArchive archive) {
        this.archives.add( archive );
    }

    public void start() throws Exception {
        super.start();
        startWeld();
    }

    protected void startWeld() {
        this.bootstrap = new WeldBootstrap();
        this.deployment = new CircusDeployment();

        this.aggregationArchive = new AggregatingBeanDeploymentArchive( "things", this.deployment.getClassLoader() );
        for (CircusBeanDeploymentArchive each : this.archives) {
            this.aggregationArchive.addMemberArchive( each );
        }
        if (this.includeCore) {
            CoreStompletBeanDeploymentArchive core = new CoreStompletBeanDeploymentArchive();
            this.aggregationArchive.addMemberArchive( core );
        }
        this.deployment.addArchive( this.aggregationArchive );
        this.bootstrap.startContainer( Environments.SE, this.deployment );
        this.bootstrap.startInitialization();
        this.bootstrap.deployBeans();
        this.bootstrap.validateBeans();

        this.beanManager = this.bootstrap.getManager( this.aggregationArchive );
    }

    public void stop() throws Exception {
        stopWeld();
        super.stop();
    }

    protected void stopWeld() {
        this.bootstrap.shutdown();
    }

    private boolean includeCore;
    private CircusDeployment deployment;
    private List<CircusBeanDeploymentArchive> archives = new ArrayList<CircusBeanDeploymentArchive>();;
    private AggregatingBeanDeploymentArchive aggregationArchive;
    private WeldBootstrap bootstrap;
    private BeanManagerImpl beanManager;
}
