package com.darksoldier1404.dsp.functions;

import com.darksoldier1404.dsp.SimplePrefix;
import com.darksoldier1404.duc.utils.ConfigUtils;
import com.darksoldier1404.duc.utils.NBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/*
 this plugin is using the API of AnvilGUI
 XD
 */
@SuppressWarnings("all")
public class DSPFunction {
    private static final SimplePrefix plugin = SimplePrefix.getInstance();
    public static final String prefix = plugin.prefix;

    public static void createPrefix(Player p, String name) {
        plugin.config.set("Settings.PrefixList." + name, "설정 필요");
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        p.sendMessage(prefix + name + " 칭호가 생성되었습니다.");
    }

    public static void deletePrefix(Player p, String name) {
        plugin.config.set("Settings.PrefixList." + name, null);
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        p.sendMessage(prefix + name + " 칭호가 삭제되었습니다.");
    }

    public static void setPrefix(Player p, String name, String[] args) {
        String text = "";
        for(int i = 2; i < args.length; i++) {
            text += args[i] + " ";
        }
        plugin.config.set("Settings.PrefixList." + name, text);
        ConfigUtils.savePluginConfig(plugin, plugin.config);
        p.sendMessage(prefix + name + " 칭호가 설정되었습니다. : " + ChatColor.translateAlternateColorCodes('&', text));
    }

    public static void showAllPrefixList(Player p) {
        p.sendMessage(prefix + "<<< 모든 칭호 목록 >>>");
        for (String key : plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false)) {
            p.sendMessage(prefix + key + " : " + ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.PrefixList." + key)));
        }
    }

    public static void showPrefixList(Player p) {
        if (plugin.udata.get(p.getUniqueId()).getList("Player.PrefixList") == null) {
            p.sendMessage(prefix + "칭호가 없습니다.");
            return;
        }
        p.sendMessage(prefix + "<<< 보유 칭호 목록 >>>");
        List<String> list = (List<String>) plugin.udata.get(p.getUniqueId()).getList("Player.PrefixList");
        for (String key : list) {
            String s = plugin.config.getString("Settings.PrefixList." + key);
            if(s != null) {
                p.sendMessage(prefix + key + " : " + ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }

    public static void equipPrefix(Player p, String name) {
        YamlConfiguration data = plugin.udata.get(p.getUniqueId());
        if(!(data.getList("Player.PrefixList") == null)) {
            try {
                List<String> list = (List<String>) data.getList("Player.PrefixList");
                if (list.contains(name)) {
                    if (data.getString("Player.Prefix") != null && data.getString("Player.Prefix").equals(name)) {
                        p.sendMessage(prefix + "이미 장착중인 칭호입니다.");
                        return;
                    }
                    data.set("Player.Prefix", name);
                    p.sendMessage(prefix + name + " 칭호가 장착되었습니다.");
                    ConfigUtils.saveCustomData(plugin, plugin.udata.get(p.getUniqueId()), p.getUniqueId().toString(), "users");
                } else {
                    p.sendMessage(prefix + "보유중인 칭호가 아닙니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("칭호 장착 오류");
                p.sendMessage(prefix + "보유중인 칭호가 아닙니다.");
            }
        }
    }

    public static void unequipPrefix(Player p) {
        YamlConfiguration data = plugin.udata.get(p.getUniqueId());
        if (data.getString("Player.Prefix") == null) {
            p.sendMessage(prefix + "장착중인 칭호가 없습니다.");
            return;
        }
        data.set("Player.Prefix", "");
        p.sendMessage(prefix + "칭호가 해제되었습니다.");
        ConfigUtils.saveCustomData(plugin, plugin.udata.get(p.getUniqueId()), p.getUniqueId().toString(), "users");
    }


    public static boolean givePrefix(Player p, String name) {
        YamlConfiguration data = plugin.udata.get(p.getUniqueId());
        if (data == null) {
            System.out.println("data is null");
            return false;
        }
        if (data.get("Player.PrefixList") != null && data.getList("Player.PrefixList").contains(name)) {
            p.sendMessage(prefix + "이미 보유중인 칭호입니다.");
            return false;
        }
        List<String> list = (List<String>) data.getList("Player.PrefixList") == null ? new ArrayList<>() : (List<String>) data.getList("Player.PrefixList");
        list.add(name);
        data.set("Player.PrefixList", list);
        p.sendMessage(prefix + name + " 칭호를 획득하였습니다.");
        ConfigUtils.saveCustomData(plugin, plugin.udata.get(p.getUniqueId()), p.getUniqueId().toString(), "users");
        return true;
    }

    public static void getPrefixCoupon(Player p, String name) {
        if (plugin.config.getConfigurationSection("Settings.PrefixList").getKeys(false).contains(name)) {
            String prefix = plugin.config.getString("Settings.PrefixList." + name);
            ItemStack item = new ItemStack(Material.valueOf(plugin.config.getString("Settings.couponMaterial")));
            ItemMeta im = item.getItemMeta();
            // set display name with placeholder
            im.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.config.getString("Settings.couponCustomName").replace("%dsp.prefix%", prefix)));
            List<String> lore = plugin.config.getStringList("Settings.couponLores");
            for (int i = 0; i < lore.size(); i++) {
                lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i).replace("%dsp.prefix%", prefix)));
            }
            im.setLore(lore);
            item.setItemMeta(im);
            item = NBT.setStringTag(item, "dsp.prefix", name);
            p.getInventory().addItem(item);
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix) + "칭호 쿠폰을 발급하였습니다.");
        }
    }
}
