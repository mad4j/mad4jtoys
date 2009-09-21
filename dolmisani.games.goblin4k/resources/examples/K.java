// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 19/09/2009 9.49.06
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import sun.audio.AudioPlayer;

public class K extends Frame
{

    public static void main(String args[])
    {
        new K();
    }

    public K()
    {
        short aword0[];
        long l3;
        super("Kaboom4K");
        a();
        setSize(640, 480);
        setVisible(true);
        aword0 = new short[8000];
        l3 = System.currentTimeMillis();
_L20:
        if(++b_int_fld % 5 == 0)
            a_int_fld = a_int_fld <= 179 ? a_int_fld + 1 : 0;
        if(a_boolean_fld) goto _L2; else goto _L1
_L1:
        if(Math.random() < a_double_fld)
        {
            int j = 0;
            do
            {
                if(j == f_float_array1d_fld.length)
                    break;
                if(f_float_array1d_fld[j] < 0.0F)
                {
                    e_float_array1d_fld[j] = c_float_fld;
                    f_float_array1d_fld[j] = 240F;
                    g_float_array1d_fld[j] = e_float_fld;
                    if(j == f_int_fld)
                        f_int_fld++;
                    break;
                }
                j++;
            } while(true);
        }
        if(b_int_fld % 20 == 0)
        {
            a_long_fld += a_long_fld <= 0L ? 0L : a_long_fld / 0x12cc03L;
            if(Math.random() < 0.5D)
                d_float_fld = -d_float_fld;
        }
        if(b_int_fld % 500 != 0) goto _L4; else goto _L3
_L3:
        h = h >= 2 ? h - 1 : 1;
        a_double_fld *= 1.25D;
        if(a_double_fld > 0.90000000000000002D)
            a_double_fld = 0.90000000000000002D;
        e_float_fld -= e_float_fld <= -1.75F ? 0.0F : 0.125F;
        d_float_fld += d_float_fld >= 0.0F ? 2 : -2;
        if(d_float_fld <= 20F) goto _L6; else goto _L5
_L5:
        this;
        20F;
          goto _L7
_L6:
        if(d_float_fld >= -20F) goto _L4; else goto _L8
_L8:
        this;
        -20F;
_L7:
        d_float_fld;
_L4:
        c_float_fld += d_float_fld;
        if(c_float_fld >= 0.0F) goto _L10; else goto _L9
_L9:
        this;
        c_float_fld + 360F;
          goto _L11
_L10:
        if(c_float_fld < 360F) goto _L2; else goto _L12
_L12:
        this;
        c_float_fld - 360F;
_L11:
        c_float_fld;
_L2:
        int i2;
        Graphics2D graphics2d;
        Graphics g1;
        int i3;
        int j3;
        int k3;
        g1 = getGraphics();
        (graphics2d = (Graphics2D)b_java_awt_image_BufferedImage_fld.getGraphics()).setColor(Color.black);
        graphics2d.fillRect(0, 0, 640, 480);
        graphics2d.setColor(Color.white);
        for(int k = 0; k < 512; k++)
        {
            if(a_double_array1d_fld[k] <= 180D)
            {
                double d1 = Math.cos(a_double_array1d_fld[k] * 0.017453292519943295D) * 240D;
                double d2 = Math.sin(b_double_array1d_fld[k] * 0.017453292519943295D) * 240D;
                graphics2d.fillOval((240 + (int)d1) - 1, (240 + (int)d2) - 1, 3, 3);
            }
            a_double_array1d_fld[k] -= 0.40000000000000002D;
            if(a_double_array1d_fld[k] < 0.0D)
                a_double_array1d_fld[k] += 360D;
        }

        graphics2d.setClip(new java.awt.geom.Ellipse2D.Float(195F, 195F, 90F, 90F));
        graphics2d.drawImage(a_java_awt_image_BufferedImage_fld, 150 - a_int_fld, 195, this);
        graphics2d.drawImage(a_java_awt_image_BufferedImage_fld, 330 - a_int_fld, 195, this);
        graphics2d.setClip(null);
        graphics2d.setColor(Color.yellow.darker());
        for(int j2 = 0; j2 < f_int_fld; j2++)
        {
            if(f_float_array1d_fld[j2] < 0.0F)
                continue;
            float f1 = f_float_array1d_fld[j2] * (float)Math.cos((double)e_float_array1d_fld[j2] * 0.017453292519943295D);
            float f2 = f_float_array1d_fld[j2] * (float)Math.sin((double)e_float_array1d_fld[j2] * 0.017453292519943295D);
            if(f_float_array1d_fld[j2] >= 53F && f_float_array1d_fld[j2] <= 62F)
            {
                int l = (float)((int)(a_float_fld + 360F) % 360);
                float f3;
                if((f3 = (int)(b_float_fld + 360F) % 360) < l)
                {
                    if(e_float_array1d_fld[j2] <= f3 || e_float_array1d_fld[j2] >= l)
                    {
                        f_float_array1d_fld[j2] = -1F;
                        if(!a_boolean_fld)
                            g_int_fld += 100;
                        for(l = 0; l < 8000; l++)
                            aword0[l] = (short)(int)((double)((i3 = 8000 - l) + i3) * Math.sin((double)l * 0.10210176124166827D));

                        a(aword0);
                        for(l = 0; l < 30; l++)
                            a(1511, 240F + f1, 240F - f2, 1.0F);

                        continue;
                    }
                } else
                if(e_float_array1d_fld[j2] >= l && e_float_array1d_fld[j2] <= f3)
                {
                    f_float_array1d_fld[j2] = -1F;
                    if(!a_boolean_fld)
                        g_int_fld += 100;
                    for(int i1 = 0; i1 < 8000; i1++)
                        aword0[i1] = (short)(int)((double)((j3 = 8000 - i1) + j3) * Math.sin((double)i1 * 0.10210176124166827D));

                    a(aword0);
                    for(int j1 = 0; j1 < 30; j1++)
                        a(1511, 240F + f1, 240F - f2, 1.0F);

                    continue;
                }
            }
            graphics2d.fillOval((240 + (int)f1) - 5, 240 - (int)f2 - 5, 10, 10);
            if(Math.random() < 0.12D)
                a(511, 240F + f1, 240F - f2, 0.0F);
            f_float_array1d_fld[j2] += g_float_array1d_fld[j2];
            if(f_float_array1d_fld[j2] < 45F && (Math.random() < 0.043999999999999997D || f_float_array1d_fld[j2] <= 0.0F))
            {
                f_float_array1d_fld[j2] = -1F;
                if(!a_boolean_fld)
                    g_int_fld -= 50;
                a_long_fld -= 0x77359400L;
                for(int k1 = 0; k1 < 8000; k1++)
                    aword0[k1] = (short)(int)(Math.random() * (double)((k3 = 8000 - k1) + k3));

                a(aword0);
                if(a_long_fld < 0L)
                {
                    a_long_fld = 0L;
                    a_boolean_fld = true;
                }
                for(int l1 = 0; l1 < 30; l1++)
                    a(511, 240F + f1, 240F - f2, 1.0F);

            }
        }

        graphics2d.setColor(Color.cyan);
        graphics2d.fillOval((240 + (int)(240F * (float)Math.cos((double)c_float_fld * 0.017453292519943295D))) - 7, 240 - (int)(240F * (float)Math.sin((double)c_float_fld * 0.017453292519943295D)) - 7, 14, 14);
        graphics2d.setStroke(new BasicStroke(4F));
        graphics2d.drawArc(c_int_fld - 55, d_int_fld - 55, 110, 110, (int)a_float_fld, (int)(b_float_fld - a_float_fld));
        i2 = 0;
          goto _L13
_L18:
        boolean flag;
        int k2;
        flag = false;
        if((k2 = a_int_array1d_fld[i2]) >= 512)
        {
            k2 -= 1000;
            flag = true;
        }
        if(k2 < 0) goto _L15; else goto _L14
_L14:
        graphics2d.setColor(new Color(flag ? 0 : k2 >> 1, 0, flag ? k2 >> 1 : 0));
        graphics2d.fillOval((int)a_float_array1d_fld[i2] - 1, (int)b_float_array1d_fld[i2] - 1, 3, 3);
        a_float_array1d_fld[i2] += c_float_array1d_fld[i2];
        b_float_array1d_fld[i2] += d_float_array1d_fld[i2];
        c_float_array1d_fld[i2] *= 0.99F;
        d_float_array1d_fld[i2] *= 0.99F;
        if(a_float_array1d_fld[i2] < 0.0F || b_float_array1d_fld[i2] < 0.0F || a_float_array1d_fld[i2] > 480F || b_float_array1d_fld[i2] > 480F)
            a_int_array1d_fld[i2] = 0;
        a_int_array1d_fld;
        i2;
        JVM INSTR dup2 ;
        JVM INSTR iaload ;
        3;
        JVM INSTR isub ;
          goto _L16
_L15:
        a_int_array1d_fld;
        i2;
        -1;
_L16:
        JVM INSTR iastore ;
        i2++;
_L13:
        if(i2 < e_int_fld) goto _L18; else goto _L17
_L17:
        graphics2d.setColor(Color.green);
        graphics2d.drawLine(480, 0, 480, 480);
        if(a_boolean_fld)
        {
            graphics2d.drawString("GAMEOVER", 525, 85);
            graphics2d.drawString("Press A Key", 523, 100);
        }
        graphics2d.drawString("Score:", 512, 150);
        graphics2d.drawString("People:", 505, 135);
        graphics2d.drawString(Integer.toString(g_int_fld), 552, 150);
        graphics2d.drawString(Long.toString(a_long_fld), 552, 135);
        g1.drawImage(b_java_awt_image_BufferedImage_fld, 0, 0, this);
        graphics2d.dispose();
        g1.dispose();
        long l2 = System.currentTimeMillis() - l3;
        l3 += l2;
        try
        {
            Thread.sleep(l2 < 47L ? 57L - l2 : 10L);
        }
        catch(InterruptedException _ex) { }
        if(true) goto _L20; else goto _L19
_L19:
    }

    private final void a()
    {
        Random random = new Random();
        a_java_awt_image_BufferedImage_fld = a(random);
        a_long_fld = 0x2540be400L;
        a_double_array1d_fld = new double[512];
        b_double_array1d_fld = new double[512];
        for(int k = 0; k < 512; k++)
        {
            a_double_array1d_fld[k] = random.nextInt(360);
            b_double_array1d_fld[k] = random.nextInt(179) - 89;
        }

        b_java_awt_image_BufferedImage_fld = new BufferedImage(640, 480, 1);
        a_float_array1d_fld = new float[512];
        b_float_array1d_fld = new float[512];
        c_float_array1d_fld = new float[512];
        d_float_array1d_fld = new float[512];
        a_int_array1d_fld = new int[512];
        e_float_array1d_fld = new float[512];
        f_float_array1d_fld = new float[512];
        g_float_array1d_fld = new float[512];
        for(int j = 0; j < 512; j++)
        {
            a_int_array1d_fld[j] = -1;
            f_float_array1d_fld[j] = -1F;
        }

        c_int_fld = 240;
        d_int_fld = 240;
        a_float_fld = 70F;
        b_float_fld = 110F;
        h = 10;
        g_int_fld = 0;
        e_int_fld = 0;
        f_int_fld = 0;
        i = 0;
        c_float_fld = 0.0F;
        d_float_fld = 2.0F;
        a_double_fld = 0.10000000000000001D;
        e_float_fld = -1.25F;
        a_boolean_fld = false;
    }

    public final boolean handleEvent(Event event)
    {
        if(event.target == this)
            if(event.id == 503)
            {
                event = (float)(Math.atan2(240 - event.y, event.x - 240) * 57.295779513082323D);
                a_float_fld = event - 20F;
                b_float_fld = event + 20F;
                if(a_float_fld > 360F && b_float_fld > 360F)
                {
                    a_float_fld -= 360F;
                    b_float_fld -= 360F;
                }
                if(a_float_fld < 0.0F && b_float_fld < 0.0F)
                {
                    a_float_fld += 360F;
                    b_float_fld += 360F;
                }
            } else
            if(event.id == 201)
                System.exit(0);
            else
            if(a_boolean_fld && event.id == 401)
                a();
        return false;
    }

    private final void a(short aword0[])
    {
        int j;
        byte abyte0[];
        short aword1[];
        aword1 = aword0;
        abyte0 = new byte[8000];
        j = 0;
          goto _L1
_L6:
        short word0;
        if((word0 = aword1[j]) < 0)
        {
            word0 = (short)(132 - word0);
            this = 127;
        } else
        {
            word0 += 132;
            this = 255;
        }
        aword0 = a_short_array1d_static_fld.length;
        for(int k = 0; k < a_short_array1d_static_fld.length; k++)
        {
            if(word0 > a_short_array1d_static_fld[k])
                continue;
            aword0 = k;
            break;
        }

        if(aword0 < 8) goto _L3; else goto _L2
_L2:
        abyte0;
        j;
        127;
          goto _L4
_L3:
        aword0 = (byte)(aword0 << 4 | word0 >> aword0 + 3 & 0xf);
        abyte0;
        j;
        aword0;
_L4:
        this;
        JVM INSTR ixor ;
        (byte);
        JVM INSTR bastore ;
        j++;
_L1:
        if(j < 8000) goto _L6; else goto _L5
_L5:
        aword0 = new ByteArrayOutputStream();
        this = new DataOutputStream(aword0);
        ".snd".getBytes();
        0;
        4;
        write();
        writeInt(25);
        writeInt(8000);
        writeInt(1);
        writeInt(8000);
        writeInt(1);
        write("a".getBytes());
        write(abyte0);
        this = new ByteArrayInputStream(aword0.toByteArray());
        AudioPlayer.player.start(this);
        return;
        JVM INSTR pop ;
    }

    private final void a(int j, float f1, float f2, float f3)
    {
        int k = j;
        j = 0;
        if(e_int_fld >= a_int_array1d_fld.length)
        {
            d_float_array1d_fld[i] = f3 * ((float)Math.random() * 2.0F - 1.0F);
            c_float_array1d_fld[i] = f3 * ((float)Math.random() * 2.0F - 1.0F);
            a_float_array1d_fld[i] = f1;
            b_float_array1d_fld[i] = f2;
            a_int_array1d_fld[i] = k;
            i++;
            if(i == a_int_array1d_fld.length)
            {
                i = 0;
                return;
            }
        } else
        {
            do
            {
                if(j == a_int_array1d_fld.length)
                    break;
                if(a_int_array1d_fld[j] < 0)
                {
                    d_float_array1d_fld[j] = f3 * ((float)Math.random() * 2.0F - 1.0F);
                    c_float_array1d_fld[j] = f3 * ((float)Math.random() * 2.0F - 1.0F);
                    a_float_array1d_fld[j] = f1;
                    b_float_array1d_fld[j] = f2;
                    a_int_array1d_fld[j] = k;
                    if(j == e_int_fld)
                    {
                        e_int_fld++;
                        return;
                    }
                    break;
                }
                j++;
            } while(true);
        }
    }

    private final BufferedImage a(Random random)
    {
        this = random.nextInt(3) + 1;
        int k1 = random.nextInt(3) + 1;
        int j = random.nextInt(2) + 1;
        Graphics g1;
        BufferedImage bufferedimage;
        (g1 = (bufferedimage = new BufferedImage(180, 90, 1)).getGraphics()).setColor(j != 1 ? new Color(127, 127, 0) : new Color(0, 0, 255));
        g1.fillRect(0, 0, 180, 90);
        g1.setColor(j != 1 ? new Color(0, 0, 255) : new Color(127, 127, 0));
        int k2 = random.nextInt(this != 3 ? 15 : 10) + (3 - this) * 9 + (this != 1 ? 1 : 7);
        for(j = 0; j <= k2; j++)
        {
            int k = random.nextInt(5) + 1;
            int j2 = random.nextInt(180);
            int l2 = random.nextInt(90);
            for(int j3 = 0; j3 <= k; j3++)
            {
                int i1;
                int l1;
                do
                {
                    i1 = (j2 + random.nextInt(9)) - random.nextInt(9);
                    l1 = (l2 + random.nextInt(9)) - random.nextInt(9);
                    this = random.nextInt(18) + 1;
                } while(i1 - this < 0 || i1 + this >= 180 || l1 - this < 0 || l1 + this >= 90);
                int k3 = this;
                for(j2 = 0; j2 < 360; j2++)
                {
                    l2 = i1 + (int)(Math.cos((double)j2 * 0.017453292519943295D) * (double)this);
                    int i3 = l1 + (int)(Math.sin((double)j2 * 0.017453292519943295D) * (double)this);
                    l2 = l2 < 180 ? l2 >= 0 ? l2 : 0 : 179;
                    i3 = i3 < 90 ? i3 >= 0 ? i3 : 0 : 89;
                    g1.drawLine(i1, l1, l2, i3);
                    l2 = this;
                    this += random.nextInt(2) - random.nextInt(2);
                    if(i1 - this < 0 || i1 + this >= 180 || l1 - this < 0 || l1 + this >= 90)
                        this = l2 - 3;
                    if(j2 > 343)
                        this = this + k3 >> 1;
                }

                j2 = i1;
                l2 = l1;
            }

        }

        int l;
        j = (l = 90 / ((4 - k1) * 3)) >> 2;
        this = random.nextInt(l * 3 >> 2) + j;
        j = random.nextInt(l * 3 >> 2) + j;
        k1 = this;
        int i2 = j;
        g1.setColor(new Color(255, 255, 255));
        for(int j1 = 0; j1 < 180; j1++)
        {
            g1.drawLine(j1, 0, j1, j);
            g1.drawLine(j1, 89, j1, 89 - this);
            this += random.nextInt(2) - random.nextInt(2);
            j += random.nextInt(2) - random.nextInt(2);
            this = ((K) (this >= 1 ? ((K) (this <= l ? this : ((K) (l)))) : 1));
            j = j >= 1 ? j <= l ? j : l : 1;
            if(j1 > 171)
            {
                this = this + k1 >> 1;
                j = j + i2 >> 1;
            }
        }

        g1.dispose();
        return bufferedimage;
    }

    BufferedImage a_java_awt_image_BufferedImage_fld;
    BufferedImage b_java_awt_image_BufferedImage_fld;
    int a_int_fld;
    int b_int_fld;
    double a_double_array1d_fld[];
    double b_double_array1d_fld[];
    int c_int_fld;
    int d_int_fld;
    float a_float_array1d_fld[];
    float b_float_array1d_fld[];
    float c_float_array1d_fld[];
    float d_float_array1d_fld[];
    int a_int_array1d_fld[];
    int e_int_fld;
    float a_float_fld;
    float b_float_fld;
    float e_float_array1d_fld[];
    float f_float_array1d_fld[];
    float g_float_array1d_fld[];
    int f_int_fld;
    int g_int_fld;
    long a_long_fld;
    int h;
    int i;
    boolean a_boolean_fld;
    float c_float_fld;
    float d_float_fld;
    double a_double_fld;
    float e_float_fld;
    static final short a_short_array1d_static_fld[] = {
        255, 511, 1023, 2047, 4095, 8191, 16383, 32767
    };

}