package com.tp.presentacion;
import com.tp.dto.ConserjeDTO;

import java.util.Scanner;

public class HuespedMenu {

    private final ConserjeDTO conserjeAutenticado;
    private final Scanner scanner;

    public HuespedMenu(ConserjeDTO conserje) {
        this.conserjeAutenticado = conserje;
        this.scanner = new Scanner(System.in);
    }
}
