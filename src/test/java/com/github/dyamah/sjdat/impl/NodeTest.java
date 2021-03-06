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

    public void testBase(){
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            assertEquals(0, Node.BASE(node.encode()));
            node.base(89);
            assertEquals(false, node.isFree());
            assertEquals(89, node.base());
            assertEquals(89, Node.BASE(node.encode()));
            node.base(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.base());
            assertEquals(1, Node.BASE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            assertEquals(0, Node.BASE(node.encode()));
            node.base(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            assertEquals(0, Node.BASE(node.encode()));
            node.base(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.base());
            assertEquals(1, Node.BASE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            assertEquals(0, Node.BASE(node.encode()));
            node.base(-1);
            assertEquals(true, node.isFree());
            assertEquals(0, node.base());
            assertEquals(0, Node.BASE(node.encode()));
            node.base(2);
            assertEquals(false, node.isFree());
            assertEquals(2, node.base());
            assertEquals(2, Node.BASE(node.encode()));
        }
    }


    public void testCheck() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            assertEquals(0, Node.CHECK(node.encode()));
            node.check(100);
            assertEquals(false, node.isFree());
            assertEquals(100, node.check());
            assertEquals(100, Node.CHECK(node.encode()));
            node.check(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.check());
            assertEquals(1, Node.CHECK(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            assertEquals(0, Node.CHECK(node.encode()));
            node.check(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            assertEquals(0, Node.CHECK(node.encode()));
            node.check(1);
            assertEquals(false, node.isFree());
            assertEquals(1, node.check());
            assertEquals(1, Node.CHECK(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(0, Node.CHECK(node.encode()));
            assertEquals(0, node.check());
            node.check(0);
            assertEquals(true, node.isFree());
            assertEquals(0, node.check());
            assertEquals(0, Node.CHECK(node.encode()));
            node.check(99);
            assertEquals(false, node.isFree());
            assertEquals(99, node.check());
            assertEquals(99, Node.CHECK(node.encode()));
        }
    }

    public void testTail() {
        {
            Node node = new Node();
            assertEquals(-1, node.tail());
            assertEquals(-1, Node.TAIL(node.encode()));
            node.tail(0);
            assertEquals( 0, node.tail());
            assertEquals( 0, Node.TAIL(node.encode()));
            node.tail(100);
            assertEquals( 0, node.tail());
            assertEquals( 0, Node.TAIL(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(-1, node.tail());
            assertEquals(-1, Node.TAIL(node.encode()));
            node.tail(-1);
            assertEquals(-1, node.tail());
            assertEquals(-1, Node.TAIL(node.encode()));
            node.tail(0);
            assertEquals(0, node.tail());
            assertEquals(0, Node.TAIL(node.encode()));
            node.tail(2);
            assertEquals(0, node.tail());
            assertEquals(0, Node.TAIL(node.encode()));
        }

    }


    public void testIsTerminal() {
        Node node = new Node();
        assertEquals(false, node.isTerminal());
        assertEquals(false, Node.TERMINAL(node.encode()));
        node.terminate();
        assertEquals(true, node.isTerminal());
        assertEquals(true, Node.TERMINAL(node.encode()));
    }

    public void testIsFree() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));
            node.base(3);
            assertEquals(false, node.isFree());
            assertEquals(false, Node.FREE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));
            node.check(3);
            assertEquals(false, node.isFree());
            assertEquals(false, Node.FREE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));
            node.tail(2);
            assertEquals(false, node.isFree());
            assertEquals(false, Node.FREE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));
            node.terminate();
            assertEquals(false, node.isFree());
            assertEquals(false, Node.FREE(node.encode()));
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));
            node.updateFreeSpaceLink(1, 3);
            assertEquals(true, node.isFree());
            assertEquals(true, Node.FREE(node.encode()));

        }
    }

    public void testUpdateFreeSpaceLink() {
        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.next());
            assertEquals( 0, node.prev());
            assertEquals( 0, Node.NEXT(node.encode()));
            node.updateFreeSpaceLink(7, 9);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(2, 8);
            assertEquals(2, node.prev());
            assertEquals(8, node.next());
            assertEquals(8, Node.NEXT(node.encode()));

            assertEquals(true, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.next());
            assertEquals( 0, node.prev());
            assertEquals(0, Node.NEXT(node.encode()));

            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(7, 9);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));

            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(2, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(-1, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));


            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(-5, -6);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));

            assertEquals(true, node.isFree());
            node.updateFreeSpaceLink(3, 2);
            assertEquals(7, node.prev());
            assertEquals(9, node.next());
            assertEquals(9, Node.NEXT(node.encode()));


            node.base(101);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.prev());
            assertEquals(-1, node.next());
            assertEquals(-1, Node.NEXT(node.encode()));

            assertEquals(false, node.isFree());
        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.prev());
            assertEquals( 0, node.next());
            assertEquals(0, Node.NEXT(node.encode()));
            node.check(101);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.prev());
            assertEquals(-1, node.next());
            assertEquals(-1, Node.NEXT(node.encode()));

            node.updateFreeSpaceLink(2, 9);
            assertEquals(false, node.isFree());
            assertEquals(-1, node.prev());
            assertEquals(-1, node.next());
            assertEquals(-1, Node.NEXT(node.encode()));


        }

        {
            Node node = new Node();
            assertEquals(true, node.isFree());
            assertEquals( 0, node.prev());
            assertEquals( 0, node.next());
            assertEquals(true, node.isFree());

            node.updateFreeSpaceLink(-2, -1);

            assertEquals(0, node.prev());
            assertEquals(0, node.next());
            assertEquals(0, Node.NEXT(node.encode()));

            node.updateFreeSpaceLink(-1, 0);

            assertEquals(0, node.prev());
            assertEquals(0, node.next());
            assertEquals(0, Node.NEXT(node.encode()));

        }
    }

}
