package com.github.dyamah.sjdat.impl;

import junit.framework.TestCase;

public class NodeTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNode() {
        Node node = new Node();
        assertEquals( true, node.isFree());
        assertEquals(false, node.isTerminal());
        assertEquals(0,  node.base());
        assertEquals(0,  node.check());
        assertEquals(-1, node.tail());
        assertEquals( 0, node.next());
        assertEquals( 0, node.prev());
    }

    public void testNodeLong() {
        {
            long c = 0;
            {
                Node node = new Node();
                assertEquals( true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(0,  node.base());
                assertEquals(0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 0, node.next());
                assertEquals( 0, node.prev());
                c = node.encode();
            }
            {
                Node node = new Node(c);
                assertEquals( true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(0,  node.base());
                assertEquals(0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 0, node.next());
                assertEquals( 0, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.base(3);
                node.check(9);
                node.tail(101);
                assertEquals( false, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(9,  node.check());
                assertEquals(101, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
                c = node.encode();
            }

            {
                Node node = new Node(c);
                assertEquals( false, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(9,  node.check());
                assertEquals(101, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.base(101);
                node.check(8);
                node.terminate();
                node.updateFreeSpaceLink(3, 9);
                assertEquals( false, node.isFree());
                assertEquals(true, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(8,  node.check());
                assertEquals(-1, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
                c = node.encode();
            }

            {
                Node node = new Node(c);
                assertEquals( false, node.isFree());
                assertEquals(true, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(8,  node.check());
                assertEquals(-1, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.updateFreeSpaceLink(3, 9);
                assertEquals(true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals( 0,  node.base());
                assertEquals( 0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 3, node.prev());
                assertEquals( 9, node.next());

                c = node.encode();
            }

            {
                Node node = new Node(c);
                assertEquals(true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals( 0,  node.base());
                assertEquals( 0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 3, node.prev());
                assertEquals( 9, node.next());


            }
        }
    }

    public void testEncode() {
        {
            long c = 0;
            {
                Node node = new Node();
                assertEquals( true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(0,  node.base());
                assertEquals(0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 0, node.next());
                assertEquals( 0, node.prev());
                c = node.encode();
            }
            {
                Node node = new Node();
                node.decode(c);
                assertEquals( true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(0,  node.base());
                assertEquals(0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 0, node.next());
                assertEquals( 0, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.base(3);
                node.check(9);
                node.tail(101);
                assertEquals( false, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(9,  node.check());
                assertEquals(101, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
                c = node.encode();
            }

            {
                Node node = new Node();
                node.decode(c);
                assertEquals( false, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(9,  node.check());
                assertEquals(101, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.base(101);
                node.check(8);
                node.terminate();
                node.updateFreeSpaceLink(3, 9);
                assertEquals( false, node.isFree());
                assertEquals(true, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(8,  node.check());
                assertEquals(-1, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
                c = node.encode();
            }

            {
                Node node = new Node();
                node.decode(c);
                assertEquals( false, node.isFree());
                assertEquals(true, node.isTerminal());
                assertEquals(101,  node.base());
                assertEquals(8,  node.check());
                assertEquals(-1, node.tail());
                assertEquals(-1, node.next());
                assertEquals(-1, node.prev());
            }
        }

        {
            long c = 0;
            {
                Node node = new Node();
                node.updateFreeSpaceLink(3, 9);
                assertEquals(true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals( 0,  node.base());
                assertEquals( 0,  node.check());
                assertEquals(-1, node.tail());

                assertEquals( 3, node.prev());
                assertEquals( 9, node.next());
                c = node.encode();
            }

            {
                Node node = new Node();
                node.decode(c);
                assertEquals(true, node.isFree());
                assertEquals(false, node.isTerminal());
                assertEquals( 0,  node.base());
                assertEquals( 0,  node.check());
                assertEquals(-1, node.tail());
                assertEquals( 3, node.prev());
                assertEquals( 9, node.next());


            }
        }

    }

    public void testBase() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            node.base(89);
            assertEquals(false, node.isFree());
            assertEquals(89, node.base());
            node.base(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.base());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            node.base(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            node.base(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.base());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            node.base(-1);
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            node.base(2);
            assertEquals(false, node.isFree());
            assertEquals(2, node.base());

        }

    }


    public void testCheck() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            node.check(100);
            assertEquals(false, node.isFree());
            assertEquals(100, node.check());
            node.check(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.check());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            node.check(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            node.check(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.check());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            node.check(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            node.check(99);
            assertEquals(false, node.isFree());
            assertEquals(99, node.check());
        }
    }

    public void testTail() {
        {
            Node node = new Node();
            assertEquals(-1, node.tail());
            node.tail(0);
            assertEquals( 0, node.tail());
            node.tail(100);
            assertEquals( 0, node.tail());
        }

        {
            Node node = new Node();
            assertEquals(-1, node.tail());
            node.tail(-1);
            assertEquals(-1, node.tail());
            node.tail(0);
            assertEquals(0, node.tail());
            node.tail(2);
            assertEquals(0, node.tail());
        }

    }


    public void testIsTerminal() {
        Node node = new Node();
        assertEquals(false, node.isTerminal());
        node.terminate();
        assertEquals(true, node.isTerminal());
    }

    public void testIsFree() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            node.base(3);
            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            node.check(3);
            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            node.tail(2);
            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            node.terminate();
            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(1, 3);
            assertEquals(true, node.isFree());
        }
    }

    public void testUpdateFreeSpaceLink() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.next());
            assertEquals( 0, node.prev());
            node.updateFreeSpaceLink(7, 9);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(2, 8);
            assertEquals(2, node.prev());
            assertEquals(8, node.next());


            assertEquals(true, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.next());
            assertEquals( 0, node.prev());

            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(7, 9);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(2, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(-1, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(-5, -6);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(3, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());


            node.base(101);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.next());
            assertEquals(-1, node.prev());

            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.next());
            assertEquals( 0, node.prev());
            node.check(101);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.next());
            assertEquals(-1, node.prev());
            node.updateFreeSpaceLink(2, 9);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.next());
            assertEquals(-1, node.prev());


        }
    }

    public void testCreateRelease() {
        {
            int m = 101;
            Node[] nodes = new Node[m];
            for(int i = 0;  i < m ; i++)
                nodes[i] = Node.create();

            for(int i = 0;  i < m ; i++)
                for(int j = 0;  j < m ; j++){
                    if (i == j){
                        assertEquals(true, nodes[i] == nodes[j]);
                    } else {
                        assertEquals(true, nodes[i] != nodes[j]);
                    }
                }
        }

        {
            int m = 101;
            Node[] nodes = new Node[m];
            for(int i = 0;  i < m ; i++)
                nodes[i] = Node.create((long)i);

            for(int i = 0;  i < m ; i++)
                for(int j = 0;  j < m ; j++){
                    if (i == j){
                        assertEquals(true, nodes[i] == nodes[j]);
                    } else {
                        assertEquals(true, nodes[i] != nodes[j]);
                    }
                }
        }

        {
            Node node0 = Node.create(3);
            Node node1 = Node.create(10);
            assertEquals(true, node0 != node1);
            Node.release(null);
            Node node2 = Node.create(9);
            assertEquals(true, node0 != node2);
            Node.release(node1);
            Node node3 = Node.create();
            assertEquals(true, node1 == node3);
            assertEquals(true, node0 != node3);
            assertEquals(true, node2 != node3);
        }
    }
}
