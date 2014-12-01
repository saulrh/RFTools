package com.mcjty.rftools.dimension;

import com.mcjty.rftools.items.dimlets.DimletItems;
import com.mcjty.rftools.items.dimlets.DimletType;
import com.mcjty.rftools.items.dimlets.KnownDimletConfiguration;

import java.util.*;

public class DimensionInformation {
    private final DimensionDescriptor descriptor;
    private final String name;

    private TerrainType terrainType;
    private Set<FeatureType> featureTypes = new HashSet<FeatureType>();

    public DimensionInformation(String name, DimensionDescriptor descriptor) {
        this.name = name;
        this.descriptor = descriptor;

        Map<DimletType,List<Integer>> dimlets = descriptor.getDimlets();
        Random random = getRandom(dimlets);
        calculateTerrainType(dimlets, random);
        calculateFeatureType(dimlets, random);
    }

    private Random getRandom(Map<DimletType, List<Integer>> dimlets) {
        int seed = 1;
        for (DimletType type : DimletType.values()) {
            for (Integer id : dimlets.get(type)) {
                seed = 31 * seed + id;
            }
        }
        return new Random(seed);
    }

    private void calculateTerrainType(Map<DimletType,List<Integer>> dimlets, Random random) {
        List<Integer> list = dimlets.get(DimletType.DIMLET_TERRAIN);
        terrainType = TerrainType.TERRAIN_VOID;
        if (list.isEmpty()) {
            // Pick a random terrain type with a seed that is generated from all the
            // dimlets so we always get the same random value for these dimlets.
            terrainType = TerrainType.values()[random.nextInt(TerrainType.values().length)];
        } else {
            terrainType = KnownDimletConfiguration.idToTerrainType.get(list.get(random.nextInt(list.size())));
        }
    }

    private void calculateFeatureType(Map<DimletType,List<Integer>> dimlets, Random random) {
        List<Integer> list = dimlets.get(DimletType.DIMLET_FEATURE);
        if (list.isEmpty()) {
            FeatureType type = FeatureType.values()[random.nextInt(FeatureType.values().length)];
            if (type != FeatureType.FEATURE_NONE) {
                featureTypes.add(type);
            }
        } else {
            for (Integer id : list) {
                featureTypes.add(KnownDimletConfiguration.idToFeatureType.get(id));
            }
        }
    }

    public DimensionDescriptor getDescriptor() {
        return descriptor;
    }

    public String getName() {
        return name;
    }

    public TerrainType getTerrainType() {
        return terrainType;
    }

    public boolean hasFeatureType(FeatureType type) {
        return featureTypes.contains(type);
    }
}
