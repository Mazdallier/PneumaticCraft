package pneumaticCraft.common.recipes;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.Pair;

import pneumaticCraft.api.recipe.AssemblyRecipe;
import pneumaticCraft.api.recipe.IPneumaticRecipeRegistry;
import pneumaticCraft.api.recipe.IPressureChamberRecipe;
import pneumaticCraft.api.recipe.IThermopneumaticProcessingPlantRecipe;
import pneumaticCraft.api.recipe.PressureChamberRecipe;
import pneumaticCraft.common.util.OreDictionaryHelper;

public class PneumaticRecipeRegistry implements IPneumaticRecipeRegistry{
    public List<IThermopneumaticProcessingPlantRecipe> thermopneumaticProcessingPlantRecipes = new ArrayList<IThermopneumaticProcessingPlantRecipe>();

    private static final PneumaticRecipeRegistry INSTANCE = new PneumaticRecipeRegistry();

    public static PneumaticRecipeRegistry getInstance(){
        return INSTANCE;
    }

    @Override
    public void registerThermopneumaticProcessingPlantRecipe(IThermopneumaticProcessingPlantRecipe recipe){
        if(recipe == null) throw new NullPointerException("Recipe can't be null!");
        thermopneumaticProcessingPlantRecipes.add(recipe);
    }

    @Override
    public void registerThermopneumaticProcessingPlantRecipe(FluidStack requiredFluid, ItemStack requiredItem, FluidStack output, double requiredTemperature, float requiredPressure){
        if(output == null) throw new NullPointerException("Output can't be null!");
        registerThermopneumaticProcessingPlantRecipe(new BasicThermopneumaticProcessingPlantRecipe(requiredFluid, requiredItem, output, requiredTemperature, requiredPressure));
    }

    @Override
    public void addAssemblyDrillRecipe(Object input, Object output){
        if(output == null) throw new NullPointerException("Output can't be null!");
        if(input == null) throw new NullPointerException("Input can't be null!");
        AssemblyRecipe.drillRecipes.add(new AssemblyRecipe(getStackFromObject(input), getStackFromObject(output)));
    }

    @Override
    public void addAssemblyLaserRecipe(Object input, Object output){
        if(output == null) throw new NullPointerException("Output can't be null!");
        if(input == null) throw new NullPointerException("Input can't be null!");
        AssemblyRecipe.laserRecipes.add(new AssemblyRecipe(getStackFromObject(input), getStackFromObject(output)));
    }

    @Override
    public void registerPressureChamberRecipe(Object[] input, float pressureRequired, ItemStack[] output){
        if(output == null) throw new NullPointerException("Output can't be null!");
        if(input == null) throw new NullPointerException("Input can't be null!");
        PressureChamberRecipe.chamberRecipes.add(new PressureChamberRecipe(input, pressureRequired, output));
    }

    public static boolean isItemEqual(Object o, ItemStack stack){

        if(o instanceof ItemStack) {
            return OreDictionary.itemMatches((ItemStack)o, stack, false);
        } else {
            String oreDict = (String)((Pair)o).getKey();
            return OreDictionaryHelper.isItemEqual(oreDict, stack);
        }
    }

    public static int getItemAmount(Object o){
        return o instanceof ItemStack ? ((ItemStack)o).stackSize : (Integer)((Pair)o).getValue();
    }

    public static ItemStack getSingleStack(Object o){
        if(o instanceof ItemStack) {
            return (ItemStack)o;
        } else {
            Pair<String, Integer> pair = (Pair)o;
            ItemStack s = OreDictionaryHelper.getOreDictEntries(pair.getKey()).get(0);
            s = s.copy();
            s.stackSize = pair.getValue();
            return s;
        }
    }

    @Override
    public void registerPressureChamberRecipe(IPressureChamberRecipe recipe){
        if(recipe == null) throw new NullPointerException("Recipe can't be null!");
        PressureChamberRecipe.specialRecipes.add(recipe);
    }

    private static ItemStack getStackFromObject(Object object){
        if(object instanceof Block) {
            return new ItemStack((Block)object);
        } else if(object instanceof Item) {
            return new ItemStack((Item)object);
        } else if(object instanceof ItemStack) {
            return (ItemStack)object;
        } else {
            throw new IllegalArgumentException("object needs to be of type Block, Item or ItemStack");
        }
    }

}
