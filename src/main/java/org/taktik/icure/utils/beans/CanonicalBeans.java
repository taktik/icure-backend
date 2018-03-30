/*
 * Copyright (C) 2018 Taktik SA
 *
 * This file is part of iCureBackend.
 *
 * iCureBackend is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * iCureBackend is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with iCureBackend.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.taktik.icure.utils.beans;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.primitives.Primitives;
import org.apache.commons.collections.map.HashedMap;
import org.taktik.icure.entities.base.Identifiable;
import org.taktik.icure.utils.beans.annotations.IgnoreProperty;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Obtains a canonical (flattened) version of a bean: bean properties become maps of properties, lists become maps, etc.
 */
// TODO This and its related methods would be better named FlattenedBeans instead of CanonicalBeans. It's clearer...
public class CanonicalBeans {

	private LoadingCache<Class, BeanInfo> beansCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).maximumSize(10000).build(new CacheLoader<Class, BeanInfo>() {
		@Override
		public BeanInfo load(@SuppressWarnings("NullableProblems") Class aClass) throws Exception {
			return Introspector.getBeanInfo(aClass, aClass.getSuperclass());
		}
	});

	/**
	 * Defines the object types (object classes) that prevent any deeper inspection of the bean tree for canonization.
	 * For example, the String type's details should not be further submitted for canonization. Primitives are also
	 * tree leaves: they should not be inspected for canonization.
	 */
	private Set<Class<?>> treeLeafTypes;

	/**
	 * Default constructor, which initializes tree leaf types with default behaviour.
	 */
	public CanonicalBeans() {
		treeLeafTypes = new HashSet<>(Primitives.allPrimitiveTypes());
		treeLeafTypes.addAll(Primitives.allWrapperTypes());
		treeLeafTypes.add(String.class);
		treeLeafTypes.add(Date.class);
		treeLeafTypes.add(Instant.class);
	}

	/**
	 * Returns a fully canonical version of a bean.
	 *
	 * @param bean The original bean that needs to be canonized
	 * @return The canonical version of that bean.
	 */
	public <T> CanonicalBean getCanonical(T bean) {
		CanonicalBean canonicalBean = new CanonicalBean();
		try {
			//noinspection unchecked
			canonicalBean.setP((Map<String, Object>) canonize(bean));

		} catch (ExecutionException | InvocationTargetException | IllegalAccessException e) {
			throw new CanonicalBeansException(e);
		}
		return canonicalBean;
	}

	private <T> Object canonize(T object) throws ExecutionException, InvocationTargetException, IllegalAccessException {
		Map<String, Object> objectTree = new HashMap<>();

		PropertyDescriptor[] propertyDescriptors = beansCache.get(object.getClass()).getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {

			if (isModifiable(propertyDescriptor)) {
				Class<?> propertyType = propertyDescriptor.getPropertyType();

				if (isTreeLeaf(propertyType)) {
					handleLeaf(object, objectTree, propertyDescriptor);

				} else if (isMap(propertyType)) {
					handleMaps(object, objectTree, propertyDescriptor);

				} else if (isSet(propertyType)) {
					handleSets(object, objectTree, propertyDescriptor);

				} else if (isList(propertyType)) {
					handleLists(object, objectTree, propertyDescriptor);

				} else {
					handleNode(object, objectTree, propertyDescriptor);
				}
			}
		}

		return objectTree;
	}

	/**
	 * Canonizes the items inside the set (if they can be canonized).
	 */
	@SuppressWarnings("unchecked")
	private Set getCanonical(Set set) throws ExecutionException, InvocationTargetException, IllegalAccessException {
		Set setOfCanonicalItems = new HashSet<>();
		for (Object setItem : set) {
			if (isTreeLeaf(setItem.getClass())) {
				setOfCanonicalItems.add(setItem);
			} else {
				setOfCanonicalItems.add(canonize(setItem));
			}
		}
		return setOfCanonicalItems;
	}

	private <T> void handleLeaf(T object, Map<String, Object> objectTree, PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException {
		String propertyName = propertyDescriptor.getName();
		Method propertyReadMethod = propertyDescriptor.getReadMethod();
		objectTree.put(propertyName, propertyReadMethod.invoke(object));
	}

	@SuppressWarnings("unchecked")
	private <T> void handleLists(T object, Map<String, Object> objectTree, PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException, ExecutionException {
		String propertyName = propertyDescriptor.getName();
		Method propertyReadMethod = propertyDescriptor.getReadMethod();

		List list = (List) propertyReadMethod.invoke(object);

		if (isListOfIdentifiableObjects(list, propertyReadMethod.getGenericReturnType())) {
			// Lists of identifiable objects are stored as a combination of maps of objects with their ID as the key, and sets of indexed objects whose value are the ids of the identifiables.
			if (list != null) {
				Map map = new HashMap<>();
				SortedSet set = new TreeSet<>();
				for (int index = 0; index < list.size(); index++) {
					Object objectFromList = list.get(index);
					map.put(((Identifiable) objectFromList).getId(), canonize(objectFromList));
					set.add(IndexedObject.build(index, ((Identifiable) objectFromList).getId()));
				}
				objectTree.put(propertyName, map);
				objectTree.put(propertyName + "Order", set);
			} else {
				objectTree.put(propertyName, new HashMap());
				objectTree.put(propertyName + "Order", new TreeSet<>());
			}
		} else {
			// Lists of unidentifiable objects are stored as sets of indexed objects. Their items are canonized
			if (list != null) {
				Set indexedObjects = new HashSet<>();
				for (int index = 0; index < list.size(); index++) {
					Class<?> itemClass = list.get(index).getClass();
					if (isTreeLeaf(itemClass)) {
						indexedObjects.add(IndexedObject.build(index, list.get(index)));
					} else {
						indexedObjects.add(IndexedObject.build(index, canonize(list.get(index))));
					}
				}
				objectTree.put(propertyName, indexedObjects);

			} else {
				objectTree.put(propertyName, new HashSet());
			}
		}
	}

	private <T> void handleMaps(T object, Map<String, Object> objectTree, PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException, ExecutionException {
		String propertyName = propertyDescriptor.getName();

		@SuppressWarnings("unchecked") Map<Object, Object> map = new HashMap<>();

		Map<Object, Object> objectMap = (Map<Object, Object>) propertyDescriptor.getReadMethod().invoke(object);

		if (objectMap!=null) {
			map.putAll(objectMap);
			// Handle the map's content
			for (Object key : map.keySet()) {
				Object o = map.get(key);
				if (o != null) {
					Class<?> valueClass = o.getClass();

					if (isTreeLeaf(valueClass)) {
						map.put(key, map.get(key));

					} else {
						// A "Set" value has to be handled differently: the set is not canonized, but its items might be!
						if (isSet(valueClass)) {
							map.put(key, getCanonical((Set) map.get(key)));

						} else if (isList(valueClass)) {
							//noinspection unchecked
							map.put(key, getCanonical(new HashSet((List) map.get(key))));

						} else {
							map.put(key, canonize(map.get(key)));
						}
					}
				}
			}
		}

		objectTree.put(propertyName, map);
	}

	private <T> void handleNode(T object, Map<String, Object> objectTree, PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException, ExecutionException {
		// This is a property which might need to be further canonized
		String propertyName = propertyDescriptor.getName();
		Method propertyReadMethod = propertyDescriptor.getReadMethod();
		Object beanProperty = propertyReadMethod.invoke(object);

		if (beanProperty != null) {
			objectTree.put(propertyName, canonize(beanProperty));
		} else {
			// Happens when the embedded bean is null
			objectTree.put(propertyName, null);
		}
	}

	private <T> void handleSets(T object, Map<String, Object> objectTree, PropertyDescriptor propertyDescriptor) throws IllegalAccessException, InvocationTargetException, ExecutionException {
		String propertyName = propertyDescriptor.getName();
		Method propertyReadMethod = propertyDescriptor.getReadMethod();

		Set set = (Set) propertyReadMethod.invoke(object);

		if (set != null) {
			if (set.size() > 0) {
				if (isSetOfIdentifiableObjects(set, propertyReadMethod.getReturnType())) {
					// Sets of identifiable objects are transformed into maps whose key is the ID
					objectTree.put(propertyName, setOfIdentifiableObjectsToMap(set));

				} else {
					// Sets of unidentifiable objects are stored as sets. Their items are canonized
					objectTree.put(propertyName, getCanonical(set));
				}

			} else {
				// Set is empty, but if it's a set of identifiables it should be put as a HashMap
				if (isCollectionOfIdentifiableObjects(propertyReadMethod)) {
					objectTree.put(propertyName, new HashMap<>());
				} else {
					objectTree.put(propertyName, new HashSet<>());
				}
			}

		} else {
			if (isCollectionOfIdentifiableObjects(propertyReadMethod)) {
				objectTree.put(propertyName, new HashMap<>());
			} else {
				objectTree.put(propertyName, new HashSet<>());
			}
		}
	}

	private boolean isCollectionOfIdentifiableObjects(Method propertyReadMethod) {
		boolean isCollectionOfIdentifiableObjects = false;
		ParameterizedType returnCollectionType = (ParameterizedType) propertyReadMethod.getGenericReturnType();
		if (ParameterizedType.class.isAssignableFrom(returnCollectionType.getActualTypeArguments()[0].getClass())) {
			Type returnCollectionElementsType = ((ParameterizedType) (returnCollectionType.getActualTypeArguments()[0])).getRawType();
			isCollectionOfIdentifiableObjects = Identifiable.class.isAssignableFrom((Class<?>) returnCollectionElementsType);
		} else if (!WildcardType.class.isAssignableFrom(returnCollectionType.getActualTypeArguments()[0].getClass()) && Identifiable.class.isAssignableFrom((Class) returnCollectionType.getActualTypeArguments()[0])) {
			isCollectionOfIdentifiableObjects = true;
		}
		return isCollectionOfIdentifiableObjects;
	}

	private boolean isList(Class<?> propertyType) {
		return List.class.isAssignableFrom(propertyType);
	}

	private boolean isListOfIdentifiableObjects(List list, Type type) {
		if (list != null) if (list.size() > 0) {
			if (Identifiable.class.isAssignableFrom(list.iterator().next().getClass())) return true;
		}
		if (type instanceof ParameterizedType) if (((ParameterizedType) type).getActualTypeArguments().length > 0) {
			Type listItemType = ((ParameterizedType) type).getActualTypeArguments()[0];
			if ((listItemType instanceof Class) && Identifiable.class.isAssignableFrom((Class<?>) listItemType)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMap(Class<?> propertyType) {
		return Map.class.isAssignableFrom(propertyType);
	}

	private boolean isModifiable(PropertyDescriptor propertyDescriptor) {
		return propertyDescriptor.getReadMethod() != null
			&& propertyDescriptor.getReadMethod().getAnnotation(IgnoreProperty.class) == null
			&& propertyDescriptor.getWriteMethod() != null
			&& propertyDescriptor.getWriteMethod().getAnnotation(IgnoreProperty.class) == null;
	}

	private boolean isSet(Class<?> propertyType) {
		return Set.class.isAssignableFrom(propertyType);
	}

	private boolean isSetOfIdentifiableObjects(Set set, Type type) {
		return set.size() > 0 && Identifiable.class.isAssignableFrom(set.iterator().next().getClass()) ||
				(type instanceof ParameterizedType) && ((ParameterizedType) type).getActualTypeArguments().length>0  && Identifiable.class.isAssignableFrom((Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0]);
	}

	private boolean isTreeLeaf(Class<?> propertyType) {
		return propertyType.isPrimitive() || treeLeafTypes.contains(propertyType);
	}

	private Map setOfIdentifiableObjectsToMap(Set set) throws ExecutionException, InvocationTargetException, IllegalAccessException {
		Map map = new HashMap<>();
		for (Object setObject : set) {
			//noinspection unchecked
			map.put(((Identifiable) setObject).getId(), canonize(setObject));
		}
		return map;
	}
}
