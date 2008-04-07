/*
 * Copyright (C) 1995-2005, Index Data ApS
 * See the file LICENSE for details.
 *
 * $Id: tstlog.c,v 1.10 2006/03/21 12:54:02 adam Exp $
 *
 */
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <yaz/options.h>
#include <yaz/log.h>
#if HAVE_UNISTD_H
#include <unistd.h>
#endif

void hook_func(int level, const char *msg, void *vp)
{
    fprintf(stderr, "[HOOK level=%d msg=%s]\n", level, msg);
}

int main(int argc, char **argv)
{
    char *arg;
    int i, ret;
    int level = YLOG_LOG;
    int number = 1;
    unsigned int wait_between_log = 0;

    while ((ret = options("f:v:l:m:n:s:w:Hh", argv, argc, &arg)) != -2)
    {
        switch (ret)
        {
        case 's':
            yaz_log_init_max_size(atoi(arg));
            break;
        case 'f':
            yaz_log_time_format(arg);
            break;
        case 'v':
            yaz_log_init_level(yaz_log_mask_str(arg));
            break;
        case 'l':
            if (!strcmp(arg, "@"))
                yaz_log_init_file(0);
            else
                yaz_log_init_file(arg);
            break;
        case 'n':
            number = atoi(arg);
            break;
        case 'm':        
            level = yaz_log_module_level(arg);
            break;
        case 'w':
            wait_between_log = atoi(arg);
            break;
        case 'H':
            yaz_log_set_handler(hook_func, 0);
            break;
        case 0:
            for (i = 0; i<number; i++)
            {
                yaz_log(level, "%d %s", i, arg);
#if HAVE_UNISTD_H
                if (wait_between_log)
                    sleep(wait_between_log);
#endif
            }
            break;
        case 'h':
        default:
            fprintf(stderr, "tstlog. Bad option\n");
            fprintf(stderr, "tstlog [-f logformat] [-v level] [-l file] "
                    "[-m module] [-w sec] [-s max] [-n num] [-H] msg ..\n");
            exit(1);
        }
    }
    exit(0);
}
/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * End:
 * vim: shiftwidth=4 tabstop=8 expandtab
 */
