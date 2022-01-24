package com.inunose.man10dice;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public final class Man10Dice extends JavaPlugin {

    ArrayList<UUID> rollingPlayers = new ArrayList<>();
    ArrayList<UUID> hiddenPlayers= new ArrayList<>();


    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("mdice").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!label.equalsIgnoreCase("mdice")) return false;

        if(!(sender instanceof Player)){
            sender.sendMessage("§4§lこのコマンドはプレイヤーでなければ実行できません");
            return false;
        }

        Player p = (Player) sender;

        if(!p.hasPermission("mdice.play")){
            p.sendMessage("§4§lあなたは権限を持っていません");
            return false;
        }

        if(args.length != 1){
            help(p);
            p.sendMessage("§4§l面を入力してください");
            return false;
        }

        if(args[0].equalsIgnoreCase("toggle")){
            if(hiddenPlayers.contains(p.getUniqueId())){
                hiddenPlayers.remove(p.getUniqueId());
                p.sendMessage("§a§l表示にします");
            }else{
                hiddenPlayers.add(p.getUniqueId());
                p.sendMessage("§4§l非表示にします");
            }
            return true;
        }

        if(hiddenPlayers.contains(p.getUniqueId())){
            p.sendMessage("§4§l/mdice toggleで表示モードにしてください");
            return false;
        }


        if(rollingPlayers.contains(p.getUniqueId())){
            p.sendMessage("§4§l現在実行中です");
            return false;
        }


        try{
            int men = Integer.parseInt(args[0]);
            int result = new Random().nextInt(men) + 1;

            rollingPlayers.add(p.getUniqueId());
            sendMessageToShow("§6§l" + p.getName() + "は" + args[0] + "面ダイスを振っています...");

            Bukkit.getScheduler().runTaskLater(this, ()-> {
                sendMessageToShow("§6§l" + p.getName() +"はダイスを振って"+ result + "が出ました!");
                rollingPlayers.remove(p.getUniqueId());
            }, 60);

        }catch (NumberFormatException e) {
            help(p);
            p.sendMessage("§4§l面は数字で入力してください");
            return false;
        }catch (IllegalArgumentException e){
            help(p);
            p.sendMessage("§4§l面の数字は１以上で入力してください");
            return false;
        }

        return true;
    }

    public void sendMessageToShow(String message){
        for(Player p: Bukkit.getOnlinePlayers()){
            if(hiddenPlayers.contains(p.getUniqueId())) continue;
            p.sendMessage(message);
        }
    }

    public void help(Player p){
        p.sendMessage("§6/mdice 面 ダイスを振ります");
        p.sendMessage("§6mdice toggle 表示非表示に切り替えます");
    }
}