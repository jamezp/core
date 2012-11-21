/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.forge.addon.dependency.Coordinate;
import org.jboss.forge.addon.dependency.DependencyFilter;
import org.jboss.forge.addon.dependency.DependencyQuery;
import org.jboss.forge.addon.dependency.DependencyRepository;

/**
 * Builds a {@link DependencyQuery} object
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public class DependencyQueryBuilder implements DependencyQuery
{
   private Coordinate coordinate;
   private String scopeType;
   private DependencyFilter dependencyFilter;
   private List<DependencyRepository> dependencyRepositories = new ArrayList<DependencyRepository>();

   protected DependencyQueryBuilder()
   {
   }

   private DependencyQueryBuilder(Coordinate coordinate)
   {
      setCoordinate(coordinate);
   }

   public static DependencyQueryBuilder create(Coordinate coordinate)
   {
      return new DependencyQueryBuilder(coordinate);
   }

   private void setCoordinate(Coordinate coordinate)
   {
      if (coordinate == null)
      {
         throw new IllegalArgumentException("Coordinate must be set");
      }
      this.coordinate = coordinate;
   }

   public DependencyQueryBuilder setFilter(DependencyFilter dependencyFilter)
   {
      this.dependencyFilter = dependencyFilter;
      return this;
   }

   public DependencyQueryBuilder setRepositories(DependencyRepository... dependencyRepositories)
   {
      return setRepositories(Arrays.asList(dependencyRepositories));
   }

   public DependencyQueryBuilder setRepositories(Iterable<DependencyRepository> dependencyRepositories)
   {
      if (dependencyRepositories != null)
      {
         for (DependencyRepository dependencyRepository : dependencyRepositories)
         {
            this.dependencyRepositories.add(dependencyRepository);
         }
      }
      return this;
   }

   public DependencyQueryBuilder setScopeType(String scopeType)
   {
      this.scopeType = scopeType;
      return this;
   }

   @Override
   public Coordinate getCoordinate()
   {
      return coordinate;
   }

   @Override
   public DependencyFilter getDependencyFilter()
   {
      return dependencyFilter;
   }

   @Override
   public List<DependencyRepository> getDependencyRepositories()
   {
      return dependencyRepositories;
   }

   @Override
   public String getScopeType()
   {
      return scopeType;
   }

}
