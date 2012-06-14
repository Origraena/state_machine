package ori.machine;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class StateMachine<E> implements Map<String,E> {

	public static final int INIT_STATE_ID = 1;
	public static final int DROP_STATE_ID = 0;

	public StateMachine() {
		_min = 0;
		_max = 127;
		_range = _max - _min +1;
		init();
	}

	public StateMachine(char min, char max) {
		_min = min;
		_max = max;
		_range = _max - _min +1;
		init();
	}

	protected void init() {
		_states = new ArrayList<State>();
		_states.add(new State(DROP_STATE_ID,false));	// drop state
		_states.add(new State(INIT_STATE_ID,false));	// init state
		_transitions = new ArrayList<int[]>(2);
		_transitions.add(new int[_range]);
		_transitions.add(new int[_range]);
		for (int i = 0 ; i < _range ; i++) {
			_transitions.get(0)[i] = DROP_STATE_ID;
			_transitions.get(1)[i] = DROP_STATE_ID;
		}
	}

	public E get(Object string) {
		if (string instanceof String)
			return process(INIT_STATE_ID, ((String)string).toCharArray(), 0);
		return null;
	}

	public E put(String key, E value) {
		char key_c[] = key.toCharArray();
		int[] curTrans;
		int currentID, nextID, key_i, transitionID, i;
		final int length = key_c.length;
		E old = null;

		key_i = 0;
		nextID = INIT_STATE_ID;
		do {
			currentID = nextID;
			transitionID = convert(key_c[key_i]);
			key_i++;
			nextID = _transitions.get(currentID)[transitionID];
		} while ((nextID != DROP_STATE_ID) && (key_i < length));


		if (key_i == length) {
			if (nextID != DROP_STATE_ID) {
				old = _states.get(nextID).value;
				_states.set(nextID,new State(nextID,true,value));
				return old;
			}
		}


		for ( ; key_i < length /*- 1*/ ; key_i++) {
			nextID = _states.size();
			_states.add(new State(nextID,false));
			_transitions.get(currentID)[transitionID] = nextID;
			curTrans = new int[_range];
			for (i = 0 ; i < _range ; i++)
				curTrans[i] = DROP_STATE_ID;
			_transitions.add(curTrans);
			transitionID = convert(key_c[key_i]);
			currentID = nextID;
		}


		old = null;
		_states.add(new State(_states.size(),true,value));
		_transitions.get(currentID)[transitionID] = _states.size()-1;
		curTrans = new int[_range];
		for (i = 0 ; i < _range ; i++)
			curTrans[i] = DROP_STATE_ID;
		_transitions.add(curTrans);



		return old;
	}

	public void clear() {
		init();
	}

	// If a contained value is null the containsKey method will return false
	public boolean containsKey(Object key) {
		return (get(key) != null);
	}

	public boolean containsValue(Object value) {
		for (State s : _states) {
			if (s.accept && value.equals(s.value))
				return true;
		}
		return false;
	}

	/** Unsupported operation */
	public Set<Map.Entry<String,E> > entrySet() {
		return null;
	}

	public boolean isEmpty() {
		return (_states.size() <= 2);
	}

	/** Unsupported operation */
	public Set<String> keySet() {
		return null;
	}

	public void putAll(Map<? extends String, ? extends E> m) {
		// TODO
		return;
	}

	public E remove(Object key) {
		// TODO
		return null;
	}

	/** "heavy" operation */
	public int size() {
		int i = 0;
		for (State s : _states)
			if (s.accept)
				i++;
		return i;
	}

	public Collection<E> values() {
		ArrayList<E> c = new ArrayList<E>();
		for (State s : _states) {
			if (s.accept)
				c.add(s.value);
		}
		return c;
	}

	protected ArrayList<State> _states;
	protected ArrayList<int[]> _transitions;
	protected int _range;
	protected char _min;
	protected char _max;

	/** Converts the char into its corresponding integer. */
	protected int convert(char c) {
		return c - _min;
	}

	protected E process(int stateID, char str[], int i) {
		if (i == str.length) {
			State state = _states.get(stateID);
			if (state.accept)
				return state.value;
			else
				return null;
		}
		return process(_transitions.get(stateID)[convert(str[i])],str,i+1);
	}

	protected class State {
		public State(int id, boolean accept) {
			this.id = id;
			this.value = null;
			this.accept = accept;
		}
		public State(int id, boolean accept, E value) {
			this.id = id;
			this.value = value;
			this.accept = accept;
		}
		public final int id;
		public final E value;
		public final boolean accept;
	}

	// DEBUG use
	public String toString() {
		StringBuilder res = new StringBuilder();
		int i,j;
		res.append("States :\n");
		for (i = 0 ; i < _states.size() ; i++) {
			res.append(i);
			res.append(" ");
			res.append(_states.get(i).accept);
			res.append(" ");
			res.append(_states.get(i).value);
			res.append('\n');
		}
		res.append("\nTransitions :\n");
		res.append("    ");
		for (j = 0 ; j < _range ; j++) {
			res.append((char)(j+_min));
			res.append(' ');
		}
		res.append('\n');
		for (i = 0 ; i < _states.size() ; i++) {
			res.append(i);
			res.append(" : ");
			for (j = 0 ; j < _range ; j++) {
				res.append(_transitions.get(i)[j]);
				res.append(',');
			}
			res.append('\n');
		}
		return res.toString();
	}

};

