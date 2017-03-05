package org.secuso.privacyfriendlywerwolf.enums;

/**
 * All game phases are defined here
 *
 * @author Tobias Kowalski <tobias.kowalski@stud.tu-darmstadt.de>
 */
public enum GamePhaseEnum {
    GAME_START(0),
    PHASE_WEREWOLF_START(1),
    PHASE_WEREWOLF_VOTING(2),
    PHASE_WEREWOLF_END(3),
    PHASE_SEER(4),
    PHASE_SEER_END(5),
    PHASE_DAY_START(6),
    PHASE_DAY_VOTING(7),
    PHASE_DAY_END(8),
    PHASE_WITCH_ELIXIR(9),
    PHASE_WITCH_POISON(10);

    private int id;

    GamePhaseEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
