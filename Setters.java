    public static void rvmBar(Player player) {
        Map<Player, BossBar> bar = main.getBar();
        if(bar.containsKey(player)){
            bar.remove(player);
            main.setBar(bar);
        }
    }

    public static void setBar(Player player) {
        Map<Player, BossBar> bar = main.getBar();
        int i = 3;
        int max = 4;
        if(!bar.containsKey(player)){
            bar.put(player, new BossBar(player, "", 100 * i / max));
            main.setBar(bar);
        }
    }
